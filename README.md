# Electronic Voting System
> JavaEE Web Applications Course Project

> Team: Echo

The Electronic Voting System (EVS) supports electronic polls. The voting system is accessible via the Web and allows an Organizer to create Polls and the respective questions for the polls. The system supports 3 different types of questions: yes-no, single choice, and multiple choice. Voters are able to abstain from voting. Participants of a poll receive a token via E-Mail when a poll is started. This token represents their ballot paper. When the voting period finishes the organizer can view the results with a graphical representation.



## Prerequisites

Before installing the application is it necessary to set up the environment and the following applications are required:

- OpenJDK 11
- Payara Server v5+
- MariaDB
- Web Browser: Chrome 84 and above, Firefox 79 and above


### Payara Server
- Create a new JDBC Connection Pools
- Create a new JDBC Resource
- Create a new JavaMail Session Resource
- Create a new Security Realm

You will find the installation guide and detailed configuration settings in our project manual. (in documents folder)

### Database
- Create a database called **echoevs**

```
create database echoevs default character set = utf8mb4;
```
- Using a user with the SUPER privilege (e.g. root) run the following command to enable the Events on the database:
```
SET GLOBAL event_scheduler = ON;
```

## Installing

For the proper working of the EVS Application it is necessary to follow installation steps provided in our project manual. (found in documents folder)


### Clone

- Clone this repo to your local machine using:

```
git clone --branch submission https://gitlab.uni-koblenz.de/echo/javaee2020-echo
```


### Setup

- Deploy and build the code, then run the EchoEVS project.

