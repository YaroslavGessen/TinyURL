tinyUrl

Server rest api for shortening web url's. Implemented with mongodb, redis and cassandra.

Test api on: https://tiny-u.herokuapp.com/swagger-ui.html
API provides:

    POST(method: createUser) - Gets user's name and ID and saves it in mongodb and cassandra DB.
    POST(method: createTinyUrl) - Gets web url, generates new tiny url for that web page and saves it in redis.
    GET(method: redirect) - You can copy and paste generated url into a web browser to check it.
    GET(method: getAllUsers) - Represents all users information. Name, ID, all urls generated by each user, and the amount of redirection(clicks) for each tiny url.
    GET(method: getClicksSummery) - Represents the users and amount of redirection(clicks) over all their tiny urls.
