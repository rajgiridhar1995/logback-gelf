/*
 * Logback GELF - zero dependencies Logback GELF appender library.
 * Copyright (C) 2019 Oliver Siegmar
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package de.siegmar.logbackgelf;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.X509TrustManager;

public class EasyX509TrustManager implements X509TrustManager {

    private final X509TrustManager delegate;

    private List<X509Certificate> trustedServerCertificates;

    public EasyX509TrustManager(final X509TrustManager delegate) {
        this.delegate = delegate;
    }

    public List<X509Certificate> getTrustedServerCertificates() {
        return trustedServerCertificates;
    }

    public void setTrustedServerCertificates(final List<X509Certificate> certificates) {
        this.trustedServerCertificates = certificates;
    }

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType)
        throws CertificateException {

        if (trustedServerCertificates != null && !trustedServerCertificates.isEmpty()) {
            validateServerCertificate(chain);
        }
    }

    private void validateServerCertificate(final X509Certificate[] chain)
        throws CertificateException {

        for (final X509Certificate cert : chain) {
            for (final X509Certificate trustedServerCertificate : trustedServerCertificates) {
                if (cert.equals(trustedServerCertificate)) {
                    cert.checkValidity();
                    return;
                }
            }
        }

        throw new CertificateException("Server certificate mismatch");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
