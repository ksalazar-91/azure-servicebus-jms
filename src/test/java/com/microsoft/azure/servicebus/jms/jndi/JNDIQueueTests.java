// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.servicebus.jms.jndi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.jms.Connection;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Reference;

import org.junit.Before;
import org.junit.Test;

import com.microsoft.azure.servicebus.jms.ServiceBusJmsConnectionFactory;
import com.microsoft.azure.servicebus.jms.ServiceBusJmsQueueConnectionFactory;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

public class JNDIQueueTests {
    ServiceBusJmsConnectionFactory sbConnectionFactory;
    ServiceBusJmsQueueConnectionFactory sbQueueConnectionFactory;
    String queueName;

    @Before
    public void testInitialize() {
        ConnectionStringBuilder connectionStringBuilder = new ConnectionStringBuilder(TestUtils.TEST_CONNECTION_STRING);
        sbConnectionFactory = new ServiceBusJmsConnectionFactory(connectionStringBuilder, null);
        sbQueueConnectionFactory = new ServiceBusJmsQueueConnectionFactory(connectionStringBuilder, null);
    }
    
    @Test
    public void storeAndCreateQueueThroughSessionTest() throws Exception {
        Connection connection = null;
        QueueConnection queueConnection = null;
        Session session = null;
        
        try {
            // ConnectionFactory -> Connection -> Session
            connection = sbConnectionFactory.createConnection();
            session = connection.createSession();
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(session.createQueue(queueName));
        } finally {
            if (session != null) session.close();
            if (queueConnection != null) queueConnection.close();
            if (connection != null) connection.close();
        }
        
        try {
            // QueueConnectionFactory -> Connection -> Session
            connection = sbQueueConnectionFactory.createConnection();
            session = connection.createSession();
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(session.createQueue(queueName));
        } finally {
            if (session != null) session.close();
            if (queueConnection != null) queueConnection.close();
            if (connection != null) connection.close();
        }

        try {
            // ConnectionFactory -> QueueConnection -> Session
            queueConnection = sbConnectionFactory.createQueueConnection();
            session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(session.createQueue(queueName));
        } finally {
            if (session != null) session.close();
            if (queueConnection != null) queueConnection.close();
            if (connection != null) connection.close();
        }
        
        try {
            // QueueConnectionFactory -> QueueConnection -> Session
            queueConnection = sbQueueConnectionFactory.createQueueConnection();
            session = queueConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(session.createQueue(queueName));
        } finally {
            if (session != null) session.close();
            if (queueConnection != null) queueConnection.close();
            if (connection != null) connection.close();
        }
    }
    
    @Test
    public void storeAndCreateQueueThroughQueueSessionTest() throws Exception {
        QueueConnection queueConnection = null;
        QueueSession session = null;

        try {
            // ConnectionFactory -> QueueConnection -> QueueSession
            queueConnection = sbConnectionFactory.createQueueConnection();
            session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(session.createQueue(queueName));
        } finally {
            if (session != null) session.close();
            if (queueConnection != null) queueConnection.close();
        }
        
        try {
            // QueueConnectionFactory -> QueueConnection -> QueueSession
            queueConnection = sbQueueConnectionFactory.createQueueConnection();
            session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(session.createQueue(queueName));
        } finally {
            if (session != null) session.close();
            if (queueConnection != null) queueConnection.close();
        }
    }
    
    @Test
    public void storeAndCreateQueueThroughJmsContextTest() throws Exception {
        JMSContext jmsContext = null;
        
        try {
            // ConnectionFactory -> JMSContext
            jmsContext = sbConnectionFactory.createContext();
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(jmsContext.createQueue(queueName));
        } finally {
            if (jmsContext != null) jmsContext.close();
        }
        
        try {
            // QueueConnectionFactory -> JMSContext
            jmsContext = sbQueueConnectionFactory.createContext();
            queueName = "MyQueue-" + UUID.randomUUID().toString().substring(0, 10);
            testStoringAndRecreatingQueue(jmsContext.createQueue(queueName));
        } finally {
            if (jmsContext != null) jmsContext.close();
        }
    }
    
    // 'physicalName' is a mandatory field, users should not be able to do JNDI without setting it on the queue.
    @Test
    public void emptyNameTest() throws Exception {
        Connection connection = sbConnectionFactory.createConnection();
        Session session = connection.createSession();
        
        Queue sbJmsQueue = session.createQueue("");
        TestUtils.testInvalidJNDIStorable((JNDIStorable)sbJmsQueue, "physicalName");
        
        sbJmsQueue = session.createQueue(null);
        TestUtils.testInvalidJNDIStorable((JNDIStorable)sbJmsQueue, "physicalName");
    }
    
    private void testStoringAndRecreatingQueue(Queue queue) throws Exception {
        JNDIStorable jndiStorable = (JNDIStorable)queue;
        Reference reference = jndiStorable.getReference();
        assertNotNull(reference);
        
        JNDIReferenceFactory referenceFactory = new JNDIReferenceFactory();
        Queue sbJmsQueue = (Queue)referenceFactory.getObjectInstance(reference, null, null, null);
        assertEquals(queueName, sbJmsQueue.getQueueName());
        
        // Test to see if the ServiceBusJmsQueue is usable by a Session
        try (Connection connection = sbConnectionFactory.createConnection()) {
            try (Session session = connection.createSession()) {
                try (MessageProducer producer = session.createProducer(sbJmsQueue)) {
                    producer.send(session.createMessage());
                }
                
                try (MessageConsumer consumer = session.createConsumer(sbJmsQueue)) {
                    consumer.receive(2000);
                }
            }
        }
        
        // Test to see if the ServiceBusJmsQueue is usable by a QueueSession
        try (QueueConnection queueConnection = sbQueueConnectionFactory.createQueueConnection()) {
            try (QueueSession session = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)) {
                try (QueueSender producer = session.createSender(sbJmsQueue);) {
                    producer.send(session.createMessage());
                }

                try (QueueReceiver consumer = session.createReceiver(sbJmsQueue)) {
                    consumer.receive(2000);
                }
            }
        }
        
        // Test to see if the ServiceBusJmsQueue is usable by a JMSContext
        try (JMSContext jmsContext = sbConnectionFactory.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            producer.send(sbJmsQueue, jmsContext.createMessage());
            
            JMSConsumer consumer = jmsContext.createConsumer(sbJmsQueue);
            consumer.receive(2000);
        }
    }
}
