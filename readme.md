(this repo is public for now)

Transaction configuration is missing but in Spring boot 3.2 - transactions are already enabled with Spring data.

I had few problems with AWS, could not ssh for a while tried few thing and so I lost some time on tasks related to deployment.

The application uses basic authentication and it doesn't use https but only http - obviously in the production we would use https and I would have invested more effort into implementing proper security. I have ran out of time to add Cucumber Component BDT tests and unit test coverage can be extended.

I have created a simple login screen but that's all about the front end that I managed to do. I have also created Swagger documentation and embedded swagger ui in the application - excluded from security, although testing endpoints through swagger will require authentication but that is fine because I also provided the "authentication" button in swagger: Below is the collection of pre-configured usernames:

user70713067
user70710473
user70710435
user70710457
user70710546
user70710547
user70710561
user70710558
user70710568
user70710565

Password is always the same: password1000

Currently for the database I am using in memory (without persistence) database h2 - obviously in production, I would use a proper RDBMS. Application has a test coverage, although having more time (and fewer problems) I would certainly improve unit test coverage and I would have added Component level BDT tests, using Cucumber - sadly I ran out of time and wasn't able to do it. I did not manage to implement the front end except for the login screen since knowing that Front ent part was not the main part of this task, I preferred to concentrate on the main aspect of the task - that is API.

In a production-level environment - this application could potentially benefit from adding a caching layer, which would allow us to reduce a few DB operations and external calls - but this was not implemented in this short demonstration project. Also as an afterthought - we could utilise "soft delete" in the implementation

I have generated SQL and liquibase scripts for the database initialization since I was asked to do it, but Currently application doesn't need them as the DB schema is being initialized during the startup of the application from the JPA model classes.

I have implemented spring security configuration - but I have also treated it leniently since it was not even a main part of the task and this project was only for demonstration purposes. In the production project, I would have placed more effort into securing the application in a better way.

Liquibase script and the sql script for DB initialization (not needed and not used by this application) can be found under "resources" folder, under main in Github.

Please, please, please I will appreciate if we would avoid excessive use of the application hosted currently on AWS as I would like to avoid accidentally exceeding my free tier allowance. I have left my ec2 instance running so that it can be tested but please - it would be awesome if you could let me know once the review is over so that I can stop my ec2 instance.

I have forgotten to provide a proper Readme file in Github.
