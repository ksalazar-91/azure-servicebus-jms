// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.servicebus.jms.ServicebusSdkDependencies;

import java.time.Duration;
import java.util.UUID;

public class ClientConstants {

	public static final int DEFAULT_OPERATION_TIMEOUT_IN_SECONDS = 30;
	public static final String NO_RETRY = "NoRetry";
    public static final String DEFAULT_RETRY = "Default";
    public static final Duration TIMER_TOLERANCE = Duration.ofSeconds(1);
    public static final Duration DEFAULT_RERTRY_MIN_BACKOFF = Duration.ofSeconds(0);
    public static final Duration DEFAULT_RERTRY_MAX_BACKOFF = Duration.ofSeconds(30);
    public static final int DEFAULT_MAX_RETRY_COUNT = 10;
    public static final int SERVER_BUSY_BASE_SLEEP_TIME_IN_SECS = 4;
    public static final UUID ZEROLOCKTOKEN = new UUID(0L, 0L);
    static final String END_POINT_FORMAT = "amqps://%s.servicebus.windows.net";
}
