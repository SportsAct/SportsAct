# Sport Connect

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description 
Objective of this app is to organize people so they can go play sports/games. So essentially it is going to connect people to play sports/games.

### App Evaluation 
[Evaluation of your app across the following attributes]
- **Category:** Lifestyle/ Social Media
- **Mobile:** This app would be primarily developed for mobile, because you are going to need to stay alert for notifications of someone inviting you to a game.
- **Story:** Creates groups for people to play at 'x' time at 'x' place, and people can say if they would be joining a game or not, in an easier way.
- **Market:** For people that play sports, and want to play sports even if they don't have a group of friends. Schools, professional sports teams, and local community parks
- **Habit:** For people that play sports weekly, they can open the app at least one a week, to be able to join the game events, and if a feed is implemented, they can see videos of people playing more often during the week, and of course they may play more than once a week.
- **Scope:** We can start scoping for creating events, and joining events, then creating a message system, and then a feed to post stuff (and if we can stretch even more a tournament system). Trying to only make it to be for sport/games.

## Product Spec  

### 1. User Stories (Required and Optional) 

**Required Must-have Stories**
 - [X] Chat portion connected to Parse database.
- [x] User's can send and receive messages from the groups they joined 
 - [x] User is able to pick a chat that they are a part of and enter it
 - [x] Upon entering a group game, user joins the group chat
 - [x] Can load events
- [x] Can create an event
- [x] User can join event
- [x] User can chat with the group
- [x] user can see a list of people who are going to the event
- [X]  Be able to include description/bio 
- [X] Be able to add a favorite sport from assortment of different sports 
 - [X] Allow user to upload a photo
 - [x] Add button to add friends 
 - [x] User is able to login
- [x] User can log out
- [X] User can sign up through a register screen to create an account
- [X] User can search other users by their username
 - [X] when user is typing, there is an auto filler with users starting by  that
 - [ ] User gets a suggestion of users on the same location
 - [ ] user can also search by real names
  - [ ] User can set up location from parse location database
 - [x] In the events user can select location
 - [ ] Events appear by location
 - [ ] Have a setting screen to let people change their language preference
- [ ] Allow user to change app notification setting in the setting screen 
- [X] Allows user to edit their profile 
- [X] Allow user to manage their event 
 - [ ] Send invitations via email or username once signed up
 - [X] User can delete event
 - [ ] User can set up location from parse location database
 - [x] In the events user can select location
 - [ ] Events appear by location


**Optional Nice-to-have Stories**

- [ ] Set Location (Google Maps Api)
- [ ] User can import contacts



### 2. Screen Archetypes 

* Login 
* Register - User signs up or logs into their account
   * Upon Download/Reopening of the application, the user is prompted to log in to gain access to their profile information to be properly matched with another person. 
   * or go to the register screen, where it can create an account
* Looking fo games screen
  * User can see the current public games or private friends games that are going to happen in the future
* Creating game screen
    * User can create games or events for others to see
* Confirmation game created screen
    * User can confirm the event that they created, and invit people to join that event 
* Messaging Screen - Chat for users to communicate (direct 1-on-1 or via group game)
   * Upon entering a group game, the user is going to be part of that group chat
   * Also users can chat between them
* Chat Screen
   * Screen where user can see all chats he is part of
* Profile Screen 
   * Allows user to upload a photo and fill in sport information that is interesting to them and others, also you can manage your friends
* Edit profile screen
    * Lets the user edit their profile
* Settings Screen
   * Lets people change language, and app notification settings.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Chat/Messaging Tab
* Home Tab
* Profile Tab

Optional:
* Google map to see games close in a map
* Feed for people to share their best plays

**Flow Navigation** (Screen to Screen)

* Login/Register is forced
   * Login to register, and register to login
   * When user logs in or registers, they will be taken to the game events feed
* Event Screen(looking for games screen)
   * Park detail screen
   * Chat of that game when user join, or wants to message the group
* Chat Screen
   * User can go to the specific message chat that he select, either a group or a friend
* Profile Screen
   * User can to a screen where he can search and add friends
   * User can go to a screen to update his profile
* Create event screen
   * When finish user go to confirmation screen
* Confirm Creation Screen
   * When user confirm, user is taken to the chat of the group
   * User has an option to invite people to join the event




![progress1](https://github.com/SportsAct/SportsAct/blob/main/walkthrough.gif)
![progress1](https://github.com/SportsAct/SportsAct/blob/main/walkthrough2.gif)
![progress1](https://github.com/SportsAct/SportsAct/blob/main/walkthroughgif1.gif)

