// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class ServiceBusJmsQueueConnectionFactory extends ServiceBusJmsConnectionFactory implements QueueConnectionFactory {

    public ServiceBusJmsQueueConnectionFactory(String connectionString, ServiceBusJmsConnectionFactorySettings settings) {
        super(connectionString, settings);
    }
    
    public ServiceBusJmsQueueConnectionFactory(ConnectionStringBuilder connectionStringBuilder, ServiceBusJmsConnectionFactorySettings settings) {
        super(connectionStringBuilder, settings);
    }
    
    public ServiceBusJmsQueueConnectionFactory(String sasKeyName, String sasKey, String host, ServiceBusJmsConnectionFactorySettings settings) {
        super(sasKeyName, sasKey, host, settings);
    }

    public QueueConnection createQueueConnection() throws JMSException {
        QueueConnection innerQueueConnection = super.createQueueConnection();
        return new ServiceBusJmsQueueConnection(innerQueueConnection);
    }

    public QueueConnection createQueueConnection(String userName, String password) throws JMSException {
        QueueConnection innerQueueConnection = super.createQueueConnection(userName, password);
        return new ServiceBusJmsQueueConnection(innerQueueConnection);
    }
}
