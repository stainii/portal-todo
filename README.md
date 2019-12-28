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

### Release
To release a module, this project makes use of the JGitflow plugin.
More information can be found [here](https://gist.github.com/lemiorhan/97b4f827c08aed58a9d8).

At the moment, releases are made on a local machine. No Jenkins job has been made (yet).