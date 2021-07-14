# HEB Interview Image Recognition Assignment
## Summary
The assignment was to create an api that can take in an image, perform object recognition on it
and saves the results in a database to be referenced in future requests.

## Stack Info
- Springboot REST API
- Imagga for Image Processing
- MYSQL Backend (Schema in src/main/resources)
- OkHTTP / SpringHateoas / Amazon S3

## Installation / Running Instructions
I hardcoded the credentials in property / class files to minimize the amount of fussiness
with environment variables, you should be able to run a gradle sync and start the api. 
It runs on port 8080 by default. There's a postman api you can use in the root of the project
to make testing this api easier. This project runs on JDK11, though it really only uses language
level 8. 

After you clone/pull this project be sure to set your
Settings->Build,Compile,Deploy->BuildTools->Gradle JDK Version to 11, 
as well as your project language level

You will need to set the S3_ACCESS_KEY and S3_SECRET_KEY environment variables to the values specified 
(I've sent an email to Ben Dean)

AWS Called me multiple times for putting my credentials in a public repo (to be fair it is a huge no-no for a normal app)
## Overall Strategy of Implementation
The main point of this was to minimize the amount of calls to Imagga. I computed the hex of the md5
checksum of each image and use that as the primary identifier for a given image. There's a chance of 
hash collisions but its vanishingly small. If this were a production build we may want to perform additional
checks on each image to ensure its actually a duplicate before we bypass S3/Imagga/DB.

Using the checksum as the identifier makes it very easy to see if it already exists in the database. If it 
does we simply return the existing entry and bypass S3/Imagga. 

**Why S3/RDS?**

S3 is a very easy implementation for a key/value store. It's very easy to host from S3 -> AWS Cloudfront and 
get true a CDN. I didn't find a strong case in a production environment where a REST API would be storing uploaded
files on its own server and I didn't think it was worth the time to implement here.

I didn't want you to have to deal with standing up a db/putting in credentials.I figured a quick RDS MYSQL
DB was the easiest solution all around.

## Things That Ate My Lunch

Most of this was really straightforward with the notable exception of fetching images that matched the set of tags
directly from the database instead of pulling out all the data and then filtering. I ended up with some crazy partitioned
join that I'll probably forget in about 3 days. 

Essentially it searches for all the tag rows that contain one of the request
object tags, and selects image rows that match that number. One of the shortfalls of JPA is sometimes when you're fetching
a large set of data you can't just throw EntityManager.find(someId) in a for loop if you're concerned about performance.

One cool thing I learned in the process was that the JVM optimizes String concatenation with calls to Stringbuilder automatically.
You still need to do explicit calls to StringBuilder if you're concatenating in a loop, otherwise the Heap gets a bunch of Stringbuilders appended,
and the GC gets overworked.

## Areas for Improvement / Talking Points
There are of course a million ways to improve this implementation. Below are areas for improvement
that immediately come to mind. 

- Authentication / Rate Limiting. Even if we wanted to expose this API for free it would be beneficial
to have rate limiting and free accounts so Imagga doesn't murder us with fees.
- If we were to use Java14 we could use Records instead of Lombok for the VO/Entity classes. I assumed HEB was on Java 8
  like most other large enterprises; so I used that. 
- We should NEVER EVER send hard coded credentials into a production codebase / version control system. 
Typically, you would use environment variables on your target machine, or something like docker-secrets/conjur
  to get these credentials where they need to be. In this case, as soon as the interview process concludes
  I'm going to make the repo private and disable the RDS/S3/Imagga credentials.
- Integration Test Suite with ReadyAPI or Postman. There's not a lot of very unit-testable logic in this API. 
  You could build out some automated services testing by uploading a known image and comparing results, and ensuring
  that you can fetch that image again after its been processed. I've included a postman project that with a little elbow 
  grease would do the trick. 
- There's a lot of small changes that I would make if this were calling more than one service such as an endpoint
formatter using variadic arguments (Basically a fancy wrapper to String.format), I'd have more things defined in the .yml
  file vs. in the code directly. My point of view on it is that sort of thing necessitates at least two service endpoints
  before you make the investment.
- Confidence Threshold parameters for the request. Seems like a useful way to filter objects, and having a default of 50
would help keep response sizes down
- Pagination! For any potentially huge dataset you would want to use pagination. There are some downsides, but if you're subject
to response time limitations, this is the way to go.