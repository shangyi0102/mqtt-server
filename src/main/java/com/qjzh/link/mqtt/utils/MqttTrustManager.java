package com.qjzh.link.mqtt.utils;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @DESC: 证书管理配置
 * @author LIU.ZHENXING
 * @date 2020年8月18日下午1:58:44
 * @version 1.0.0
 * @copyright www.7g.com
 */
public class MqttTrustManager implements X509TrustManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final X509TrustManager trustManager;
	
	private final KeyStore keyStore;

	public MqttTrustManager(InputStream certFile) throws Exception {
		this.keyStore = initKeyStore(certFile);

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
		trustManagerFactory.init((KeyStore) null);
		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
		this.trustManager = (X509TrustManager) trustManagers[0];
	}

	private KeyStore initKeyStore(InputStream file) throws Exception {
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null);

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

		X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(file);

		trustStore.setCertificateEntry(cert.getSubjectX500Principal().getName(), cert);

		return trustStore;
	}

	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		try {
			this.trustManager.checkServerTrusted(chain, authType);
		} catch (CertificateException e) {
			try {
				X509Certificate[] reorderedChain = reorderCertificateChain(chain);

				CertPathValidator validator = CertPathValidator.getInstance("PKIX");

				CertificateFactory factory = CertificateFactory.getInstance("X509");
				CertPath certPath = factory.generateCertPath(Arrays.asList((Certificate[]) reorderedChain));
				PKIXParameters params = new PKIXParameters(this.keyStore);
				params.setRevocationEnabled(false);
				validator.validate(certPath, params);
			} catch (CertificateNotYetValidException ee) {
				logger.error("certificate not yet valid exception", ee);
				return;
			} catch (Exception ex) {

				Throwable cases = ex.getCause();
				if (cases instanceof CertificateNotYetValidException) {
					logger.error("validate cert failed.because system is early than cert valid "
							+ ". wsf will ignore this exception,", ex);
					return;
				}
				logger.error("validate cert failed.", ex);
				throw e;
			}
		}
	}

	private X509Certificate[] reorderCertificateChain(X509Certificate[] chain) {
		X509Certificate[] reorderedChain = new X509Certificate[chain.length];
		List<X509Certificate> certificates = Arrays.asList(chain);

		int position = chain.length - 1;
		X509Certificate rootCert = findRootCert(certificates);
		reorderedChain[position] = rootCert;

		X509Certificate cert = rootCert;
		while ((cert = findSignedCert(cert, certificates)) != null && position > 0) {
			reorderedChain[--position] = cert;
		}

		return reorderedChain;
	}

	private X509Certificate findRootCert(List<X509Certificate> certificates) {
		X509Certificate rootCert = null;

		for (X509Certificate cert : certificates) {
			X509Certificate signer = findSigner(cert, certificates);
			if (signer == null || signer.equals(cert)) {
				rootCert = cert;

				break;
			}
		}
		return rootCert;
	}

	private X509Certificate findSignedCert(X509Certificate signingCert, List<X509Certificate> certificates) {
		X509Certificate signed = null;

		for (X509Certificate cert : certificates) {
			Principal signingCertSubjectDN = signingCert.getSubjectDN();
			Principal certIssuerDN = cert.getIssuerDN();
			if (certIssuerDN.equals(signingCertSubjectDN) && !cert.equals(signingCert)) {
				signed = cert;

				break;
			}
		}
		return signed;
	}

	private X509Certificate findSigner(X509Certificate signedCert, List<X509Certificate> certificates) {
		X509Certificate signer = null;

		for (X509Certificate cert : certificates) {
			Principal certSubjectDN = cert.getSubjectDN();
			Principal issuerDN = signedCert.getIssuerDN();
			if (certSubjectDN.equals(issuerDN)) {
				signer = cert;

				break;
			}
		}
		return signer;
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}
}
