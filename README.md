# APP_NAME_HERE

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
- **Market:** For people that play sport, and want to play sport even if they don't have a group of friends.
- **Habit:** For people that play sports weekly, they can open the app at least one a week, to be able to join the game events, and if a feed is implemented, they can see videos of people playing more often during the week, and of course they may play more than once a week.
- **Scope:** We can start scoping for creating events, and joining events, then creating a message system, and then a feed to post stuff (and if we can stretch even more a tournament system). Trying to only make it to be for sport/games.

## Product Spec  

### 1. User Stories (Required and Optional) 

**Required Must-have Stories**

* User can create a profile within the app 
    * Set username/name
    * set sport preferences
    * look for friends
* Users can create events (private or public) and send invitations via email or username once signed up. If the person invited does not have an account, they will be prompted to create one.
    * creating events will also create a groupchat for that event


**Optional Nice-to-have Stories**

* Set Location (Google Maps Api)
* User can import contacts

### 2. Screen Archetypes 

* Login 
* Register - User signs up or logs into their account
   * Upon Download/Reopening of the application, the user is prompted to log in to gain access to their profile information to be properly matched with another person. 
   * or go to the register screen, where it can create an account
* Looking fo games screen
  * User can see the current public games or private friends games that are going to happen in the future
* Creating game screen
    * User can create games or events for others to see 
* Messaging Screen - Chat for users to communicate (direct 1-on-1 or via group game)
   * Upon entering a group game, the user is going to be part of that group chat
   * Also users can chat between them
* Profile Screen 
   * Allows user to upload a photo and fill in sport information that is interesting to them and others, also you can manage your friends
* Settings Screen
   * Lets people change language, and app notification settings.


### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Chat/Messaging Tab
* Home Tab
* Profile Tab

**Flow Navigation** (Screen to Screen)

* Home Screen
   * from home screen user can create a new event
   * ...
* Event Created Screen
   * user defines the details of the even
   * ...
 * Group Messaging Screen
   * Once event is created user can invite friends
   and chat with them.  
   * Friends can accept or decline invitation ??
 * Profile Screen
   * User can change profile settings and view frineds
   * ...

## Wireframes 
[Add picture of your hand sketched wireframes in this section]
<img src= "https://mail.google.com/mail/u/1/?ui=2&view=btop&ver=1j3j1tobv9w1#attid%253Datt_178330e50b87d1f8_0.1_8054eba0_f7fc0910_b3f62c97_93d0e29e_750b716d%25252FPHOTO-2021-03-14-18-21-43.jpg">
<img src="https://mail.google.com/mail/u/1/?ui=2&view=btop&ver=1j3j1tobv9w1#attid%253Datt_178330eafde57670_0.1_dda7a95c_cc026d3a_8f672210_b07e0760_4269b4e7%25252FPHOTO-2021-03-14-18-21-53.jpg" width=600>


### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
