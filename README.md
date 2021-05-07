# SeniorProject (BLIND STICK)
This part represents the software component  of this project, it is considered as one of the important parts of this project as it deals  with many components in software development, this part mainly leverages an online database and mobile development so it can obtain real-time data using hardware components, and sending it to the online database which plays the major role in sending data to the mobile application.
The application until now is considered as API consumer. It takes the data from back end database, do some process and update location of users on map.

APIs (Map API):
• Application is displaying map on screen, then adding some interaction by allowing to draw line between current location and destination using line parametric function equation.

Speech API:
• Facilitate the Search for places in the app by using voice commands to search instead of just search manually.

Architecture components, Design patterns:
• Application consists of Login, Signup, Map and Search activities that control the flow of the whole process.
 
Database (Firebase Firestore):
• Application has used online database as a source of truth.each time the location of the current user changes,the app instantly loads that location into database
and whenever he needs that location again, it asks database for that location.

SQL lite, Shared preferences
• Incorporated persistent data storage leveraging user data cashing.

