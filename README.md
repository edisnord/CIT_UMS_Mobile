# CIT UMS Android port
This is a simple app i built with the intention of providing a decent mobile experience for the creatively named \"University Management System\" university grade management system our school provides as a midterm project for my Fundamentals Of Programming II class. The program uses the web scraper library JSoup to log into CIT\'s UMS(ums.cit.edu.al) using HTTP requests, and scraping all of the students grades and profile information from the HTTP responses recieved by different GET requests to different php pages of the site. The intent of this project was to practice webscraping, Android development and lambdas/streams as a replacement to loops in the processing of information in collections, and also, to a certain degree, a \"Community service\" to the students of my college.

## Known issues
1. Minor UI glitches in smaller screens

## Possible Future features
1. Notification support for newly added grades
2. Contact page listing all the professors who have added grades on your profile, running the mail creation activity on your preferred mail app when the user decides to mail a professor
3. UI improvements in the Grades By Subject page, removing the table and creating Fragments to display the information of a subject.

## How to download
In the release page of this GitHub repository. You need to enable installation from unknown sources in your settings app first though in order to install the apk file downloaded(the way you do this changes from rom to rom, i would suggest googling how to do it)
