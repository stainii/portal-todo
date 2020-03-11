# portal-todo
[![Build Status](https://server.stijnhooft.be/jenkins/buildStatus/icon?job=portal-todo/master)](https://server.stijnhooft.be/jenkins/job/portal-todo/job/master/)

A todo app? Yes, but a smart one.

## Inspired by "The 7 Habits of Highly Effective People"
This todo app is built around the principles of the famous 7 habits book, dividing tasks in 4 categories:

* Urgent, important             => Fires to extinguish
* Not urgent, important         => The tasks that help you the most
* Urgent, not important         => Tasks to be done sometime, but to be avoided in the future
* Not urgent, not important     => Tasks to never look at again, unless I'm really bored

## Task templates
Some tasks always lead to other tasks.
For example, when organizing an event, multiple tasks have to be created: send out invitations, agree on a date, get everything ready, follow-up afterwards how everyone experienced the event, ...

This app allows me to define task templates. When creating a task, I can select a task template, which results in multiple, relevant tasks.
No more forgetting to follow up on something!

## Task patches
This application should support
* **offline use**
* use on **multiple devices** (which means: the list of tasks should be up to date on all devices on all times)

These are 2 conflicting requirements.

### Conflicting changes
If I'm offline and I edit a task, this edit cannot be sent to the server until I get online.
It can take hours or days before the change gets pushed. 

What if I make another change to the same task on another device, that gets synced before my first change gets synced?

To mediate this problem, the applications should **not send complete tasks** to the server, but **task patches**.

### What are task patches?
Task patches contain **a datetime** and a **description of the changes** that need to be made.
These task patches get **applied to the patch**, **in order** of their datetime.

An example:

**Task:**
``` json
{
    "id": "abc",
    "name": "my fancy task",
    "context": "Personal",
    "creationDateTime": "2020-03-01 12:00:00",
    "history": [
        "id": "aaa",
        "taskId": "abc",
        "dateTime": "2020-03-01 12:00:00",
        "changes": {
            "name": "my fancy task",
            "context": "Personal"
        }
    ]
}
```

**Task patch A:**
``` json
{
    "id": "def",
    "taskId": "abc",
    "dateTime": "2020-03-10 07:00:00",
    "changes": {
        "name": "my superfancy task",
        "description": "Isn't this a fancy task?"
    }
}
```

**Task patch B:**
``` json
{
    "id": "def",
    "taskId": "abc",
    "dateTime": "2020-03-11 07:00:00",
    "changes": {
        "name": "my great task"
    }
}
```

**Resulting task:**
``` json
{
    "id": "abc",
    "name": "my great task",
    "context": "Personal",
    "description": "Isn't this a fancy task?"
    "creationDateTime": "2020-03-01 12:00:00",
    "history": [
        {
            "id": "aaa",
            "taskId": "abc",
            "dateTime": "2020-03-01 12:00:00",
            "changes": {
                "name": "my fancy task",
                "context": "Personal"
            }
        }, {
            "id": "def",
            "taskId": "abc",
            "dateTime": "2020-03-11 07:00:00",
            "changes": {
                "name": "my great task"
            }
        }, {
           "id": "def",
           "taskId": "abc",
           "dateTime": "2020-03-11 07:00:00",
           "changes": {
               "name": "my great task"
           }
       }
    ]
}
```

It doesn't matter in task patch A gets sent to the server before task patch B. Even if A arrives later than B, they will be (re)applied in order of their date.


### Release
#### How to release
To release a module, this project makes use of the JGitflow plugin and the Dockerfile-maven-plugin.

1. Make sure all changes have been committed and pushed to Github.
1. Switch to the dev branch.
1. Make sure that the dev branch has at least all commits that were made to the master branch
1. Make sure that your Maven has been set up correctly (see below)
1. Run `mvn jgitflow:release-start -Pproduction`.
1. Run `mvn jgitflow:release-finish -Pproduction`.
1. In Github, mark the release as latest release.
1. Congratulations, you have released both a Maven and a Docker build!

More information about the JGitflow plugin can be found [here](https://gist.github.com/lemiorhan/97b4f827c08aed58a9d8).

##### Maven configuration
At the moment, releases are made on a local machine. No Jenkins job has been made (yet).
Therefore, make sure you have the following config in your Maven `settings.xml`;

````$xml
<servers>
		<server>
			<id>docker.io</id>
			<username>your_username</username>
			<password>*************</password>
		</server>
		<server>
			<id>portal-nexus-releases</id>
			<username>your_username</username>
            <password>*************</password>
		</server>
	</servers>
````
* docker.io points to the Docker Hub.
* portal-nexus-releases points to my personal Nexus (see `<distributionManagement>` in the project's `pom.xml`)
