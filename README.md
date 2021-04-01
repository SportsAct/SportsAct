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
- **Market:** For people that play sport, and want to play sport even if they don't have a group of friends.
- **Habit:** For people that play sports weekly, they can open the app at least one a week, to be able to join the game events, and if a feed is implemented, they can see videos of people playing more often during the week, and of course they may play more than once a week.
- **Scope:** We can start scoping for creating events, and joining events, then creating a message system, and then a feed to post stuff (and if we can stretch even more a tournament system). Trying to only make it to be for sport/games.

## Product Spec  

### 1. User Stories (Required and Optional) 

**Required Must-have Stories**
[x]   User can log into account by entering username and password
[x]   User can create a new account
[ ]   User can create a profile within the app 
         * Set username/name
         * set sport preferences
         * look for friends
[x]   Users can create events (private or public) and 
[ ]    Once event is created, users can send invitations via email/username  If person invited does not have an account, they will be prompted to create        one.
         * creating events will also create a groupchat for that event
[ ]   User can send and receive message from the groups they are join
[ ]   Users can see a list of events from their friends
         * Also they can join the events, and write to that group chat 

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
   * When user login or register will be taken to the game events feed
* Event Screen(looking for games screen)
   * Park detail screen (stretch story)
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

## Wireframes 
<img src="https://github.com/SportsAct/SportsAct/blob/main/PHOTO-2021-03-14-18-21-43.jpg" width="800" height="750">
<img src="https://github.com/SportsAct/SportsAct/blob/main/PHOTO-2021-03-14-18-21-53.jpg" width="800" height="750">

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models

#### Users
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     |UserID              |
|  name             | Text     |Real Name of user              |
|  username         | Text     |in app username              |
|  profile picture  | file     |profile picture              |
|  sport preferences| text[]   |list of sport preferences    |
|  bio              | text     |short bio                    | 
|  location         | location |where user is going to play  | 
 
 #### Events
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     |eventID              |
|  location         | Text     |location of event              |
|  time             | Date     |date of event              |
|  createdAt        | Date     |creation of event              |
|  title            | text     |a title              |
|  sport            | text     |sport to be played              | 
|  author           | user     |host of event              | 
|  amount of users  | integer  |number of user in event              | 
|  privacy          | bool     |public or private event              | 

 #### Chats
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     |chatID              |
|  event            | Event    |event of chat              |
|  createdAt        | Date     |creation of chat              |

#### Message
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     | messageID             |
|  chat             | Chat     |the chat of the message              |
|  time             | Date     |when send              |
|  attachment       | File     |if any pic              |
|  message          | text     |body of message              |
|  user             | user     |sender of message              |

#### EventParticipant
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     | EventParticipantID             |
|  user             | user     |participant of event              |
|  event            | event    |event user is enrolled              |

#### ChatUserJoin
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     |ChatUserJoin             |
|  user             | user     |participant of chat              |
|  event            | event    |chat user is enrolled              |

### Stretch Models:

#### Post
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     | postID             |
|  author           | user     |creator              |
|  caption          | Text     |caption of post              |
|  createdAt        | Date     | creation time             |
|  image            | file     |image of post              |
|  location         | location |where the post is from              |
|  likecount        | int      |like count              | 
|  commentcount     | int      |comments count              |

 #### Likes
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     |likeID              |
|  user             | user     | user that like             |
|  post             | post     | post that was liked             |

#### Comments
| Property          |Type      | Description  |
|      ---          | ---      |---           |
|  id               | Text     | commentID              |
|  user             | user     | user that comment             |
|  post             | post     | post that was commented             |

### Networking
  
  Home Page
  -[Read/GET] Gets all the feed
 
 <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/getHomeFeed.PNG" width="1200" height="500">
 
 -[Read/GET] Gets all the friends of a user
 
  <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/getFriends.PNG" width="1200" height="500">
  
  Chat
  
  -[Read/get] Gets all messages
  
  <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/getMessages.PNG" width="1200" height="500">
  
  -[Create/Post] Post a message
  
  <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/sendingAMssage.PNG" width="1200" height="500">
  
  -[Create/Post] Join the event
  
  <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/UserJoiningEvent.PNG" width="1200" height="500">
  
  Profile
  
  -[Update/put] Update the profile 
  
  <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/updateProfile.PNG" width="1200" height="500">
  
  Confirm Create Event Screen
  
  -[Create/Post] create an event
  
   <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/CreatingEvent.PNG" width="1200" height="500">
  
  -[Create/Post] create a chat
  
  <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/createChat.PNG" width="1200" height="500">
  
  Add Friend
  
  -[Read/Get] username (stretch to search by also name)
  
   <img src="https://github.com/SportsAct/SportsAct/blob/main/Network%20Snippets/searchByUsername.PNG" width="1200" height="500">
  
- [OPTIONAL: List endpoints if using existing API such as Yelp]


![progress1](https://user-images.githubusercontent.com/67083832/112782627-4f516200-9002-11eb-88e0-a1fe7bb6b921.gif)
