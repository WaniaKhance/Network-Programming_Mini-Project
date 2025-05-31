# Network-Programming_Mini-Project
## Java-Based Multi-Threaded Publish-Subscribe Messaging System

This project is completed by a group of 5 students of EMJMD GENIAL Programme. 
It involves the implementation of a system that can handle a need for two or more clients to communicate anonymously over a public publish-subscribe system.

## Summary

This Java project implements a basic publish-subscribe messaging system using socket programming. It mimics a simplified version of how publishers and subscribers interact in a message broker system, such as those used in event-driven architectures or pub-sub communication models.

### Core Components and Responsibilities

**1. Server**
  - Acts as a central message broker or manager.
  - Listens for client connections on a server socket.
  - For each new connection (whether publisher or subscriber), it starts a new ServerThread to handle it concurrently.
  - Determines whether the connected client is a Publisher or a Subscriber.
    - Publishers: Accepts a topic and message, then relays the message to all subscribers subscribed to that topic.
    - Subscribers: Registers the client’s interest in a topic and keeps the connection open to receive messages published on that topic.
  - Uses shared data structures (like a map from topics to subscriber lists) to manage subscriptions and message delivery.

**2. Publisher**
  - Connects to the server and sends a topic along with a message.
  - Identifies itself as a publisher when connecting.
  - Sends out content to be distributed to all relevant subscribers.

**3. Subscriber**
  - Connects to the server and subscribes to a specific topic.
  - Waits to receive and print messages that are published under the subscribed topic.
  - Identifies itself as a subscriber upon connecting.

### How It Works

  - Server starts and listens for incoming connections.
  - A Subscriber connects and subscribes to a topic.
  - A Publisher connects and publishes a message to the same topic.
  - The server receives the message from the publisher and forwards it to all subscribers of that topic.
