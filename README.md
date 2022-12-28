# Work in progress.
   But it is fully functional now.

# What for?
This application being developed for simplifying bulk coffee purchase process from the local <a href="https://tastycoffee.ru/"> coffee roasters</a>

# How it works?
- Application uses your tastycoffee account for website access and coffee price list parsing. Price list updates every three hours.
- Bulk purchase customers (users) creates their orders using provided telegram bot.
- Session manager manually closes session, or session being closed automatically by reaching provided session close date.
- Application calculates all orders price including discounts and sends corresponding messages for all bulk-purchase session participants.
- Application places all customers orders to the manager tastycoffee account.
- Application provides per-customer order payment tracking and notifications.
- Session manager manually validates placed order, puts any required corrections and places order.

# How to start using?
First, create <code>secrets.properties</code> file, which contains your organization telegram bot token and name, your tastycoffee account name and password using provided template.

Second, run this app using one of three available ways:

<details>
  <summary>Run using docker compose</summary>

   ```sh
   docker compose up
   ```
</details>

<details>
  <summary>Build and run locally</summary>
Please do not forget to install, configure and start all requirement dependencies.

1. Clone the repo
   ``` sh 
   git clone https://github.com/AlxStenshin/TastyCoffeeBulkPurchase.git
   ```
2. Navigate to source dir
   ``` sh
   cd TastyCoffeeBulkPurchase/
   ```
3. Build
   ``` sh
   ./gradlew clean build
   ```
4. Build Jar and start the application
    ```sh
    ./gradlew bootJar && java -jar build/libs/TastyCoffeeBulkPurchase.jar
    ```
</details>

<details>
  <summary>ToDo: Heroku? deploy.</summary>
</details>

Then create new purchase session using provided web interface available at localhost:8080 by default.

Next, share your bot with your company employees or any other bulk purchase participants.

That's it. All orders will be automatically placed after session being closed.

# ToDo:
1) Auto-close session by reaching session close date.
2) "Session will be closed in 1 hour" customer notification.
3) ~~"New session started" customer notification.~~
4) "Session completely paid" (closed, finished) customer notification.
5) ~~"Product changed" notification message.~~
6) Do not parse products until there is closed, but non-finished session (this will clear purchases otherwise)
7) ~~Session summary in human-readable format for order validation.~~
8) Separate order placement for grindable and non-grindable coffee. (TastyCoffee manager requirements, thanx for that :<)
9) JVM, DB and app-specific metrics monitoring using Prometheus and Grafana.
10) Docker compose
11) Heroku? deploy