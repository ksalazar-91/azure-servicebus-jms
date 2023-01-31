// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.servicebus.jms.ServicebusSdkDependencies;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class Util {
	
    private Util() {
    }

    public static URI convertNamespaceToEndPointURI(String namespaceName) {
        try {
            return new URI(String.format(Locale.US, ClientConstants.END_POINT_FORMAT, namespaceName));
        } catch (URISyntaxException exception) {
            throw new IllegalConnectionStringFormatException(
                    String.format(Locale.US, "Invalid namespace name: %s", namespaceName),
                    exception);
        }
    }
}