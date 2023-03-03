# Work in progress.
   But it is fully functional now.

# What for?
This application being developed for simplifying bulk coffee purchase process from the local <a href="https://tastycoffee.ru/"> coffee roasters</a>

# How it works?
- Application uses your tastycoffee account for website access and coffee price list parsing. Price list updates every three hours.
- Bulk purchase customers (users) creates their orders using provided telegram bot.
- Session manager manually closes session, or session being closed automatically by reaching provided session close date.
- Application calculates all orders price including discounts and sends corresponding messages for all bulk-purchase session participants.
- Application automatically places all customers orders.
- Application provides per-customer order payment tracking and notifications.
- Session manager manually validates placed order, puts any required corrections and places order.

# How to start using?

First: get the code:
```sh
   git clone https://github.com/AlxStenshin/TastyCoffeeBulkPurchase.git
   cd TastyCoffeeBulkPurchase/
```

Second, configure the secrets:
```sh
nano src/main/resources/properties/secrets.yml
```

You have to set at least your telegram bot token and name, your tastycoffee account name and password.

Default secrets configuration listed below:
```
tasty-coffee:
  user-name: some@mail.org
  password: very_long_secure_password

telegram-bot:
  token: 0000000000:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  name: "@YourBot"

spring:
  datasource:
    username: postgres
    password: postgres
```

Finally, build and run this app using docker compose:

   ```sh
   ./gradlew clean build
   ./gradlew bootJar
   docker compose up
   ```

Anyway, you can run app locally using your favorite IDE, but don't forget to install and configure Postgres and Redis first.<br />
Session manager web interface available at <a href="localhost:8080">localhost:8080</a><br />
Please note, only one active session allowed. Please close and finish any open session before opening the new one.<br />
You can open new session by clicking corresponding link in the top left corner of the window:

![no-sessions-found.png](assets%2Fno-sessions-found.png)
It makes no sense creating already closed or finished session, so please leave these checkboxes unchecked.<br />
Closed session means that no one can add, remove or edit their orders before session orders placement.<br />
Finished session means fully paid session.

![new-session.png|80%](assets%2Fnew-session.png)
You will be redirected to the index page after successful session creation.

![session-created.png](assets%2Fsession-created.png)

Next, share your bot with your company employees or any other bulk purchase participants.<br />
Basic telegram bot user workflow: <br/>
[![Watch the video](https://img.youtube.com/vi/SH17j_o1-ug/hqdefault.jpg)](https://youtu.be/SH17j_o1-ug)<br />

That's it. All orders will be automatically placed after session being closed on reaching session close date.

Also, you can manage selected session state by clicking "edit session" button and selecting corresponding action on the next screen.
![edit-session.png](assets%2Fedit-session.png)


"Close with customer notification" button can be used for manual session closing with corresponding telegram messages publishing for all active session users.<br />
This action abuse can be very annoying for the bot users, please be polite.
This action can be useful if you need to close session before the close date.

Each of "Place purchases" buttons launches two actions with corresponding product filter selected on the next screen.
1) Webpage order placement.
2) Text file report creation for manual order validation.

Product are filters useful for partial order placement.<br />
Some of tasty-coffee managers can ask you to put fine grind coffee as separate order, for example.

|                        Exclude filter                        |                        Include filter                        |
|:------------------------------------------------------------:|:------------------------------------------------------------:|
| ![exclude-goods-types.png](assets%2Fexclude-goods-types.png) | ![include-goods-types.png](assets%2Finclude-goods-types.png) |


# ToDo:
1) Do not parse products until there is closed, but non-finished session (this will clear purchases otherwise)
2) Per-customer text file order for each closed session.
3) Make Log messages more readable
4) JVM, DB and app-specific metrics monitoring using VictoriaMetrics and Grafana.
