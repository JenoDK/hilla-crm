# Implementing OAuth2 authentication in a stateless hilla app

This project aims to implement oauth together with the stateless authentication in Hilla.

Initially I implemented this with a react application as seen [here](https://github.com/JenoDK/react-spring-app)
and wanted to try this for Hilla too.

## Dependencies

We add `spring-boot-starter-oauth2-client` to our [pom.xml](pom.xml).

## Properties

Add your client properties to the application.yml, in my case I added src/main/resources/application-dev.yml and added it to [.gitignore](.gitignore).
Check [this article](https://www.callicoder.com/spring-boot-security-oauth2-social-login-part-1/#creating-oauth2-apps-for-social-login) to see how to set up these apps in facebook, google or github.

Example:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: <my_id>
            client-secret: <my_secret>
            scope:
              - email
              - profile
          facebook:
            client-id: <my_fb_id>
            client-secret: <my_fb_secret>
            scope:
              - email
              - public_profile
```

**Note:** secrets should never be committed to your remote repository!


## Client-side

Except for adding an extra button on the [login screen](frontend/views/login/login-view.ts) to login with f.e. Google one more change was needed:
The routing had to allow 'oauth2/authorization/google' and not try to find a component that matches this.
So this was added
```typescript
{
    path: 'oauth2/authorization/google',
    action: (_: Context, commands: Commands) => {
        window.location.pathname = _.pathname;
    },
},
```

## Server-side

### Security configuration

We can just add the `oauth2Login()` section to the `configure` method in [SecurityConfiguration](src/main/java/com/jeno/application/security/SecurityConfiguration.java).

In [this article](https://www.callicoder.com/spring-boot-security-oauth2-social-login-part-2/) we see that we'd need to add some way to store the JWT token as a cookie. 
With our vaadin security config this is not needed since it takes care of all that already, see VaadinStatelessSecurityConfigurer.class for more info.

Note: the redirect success handler does not work together well with our client side code. The reason is that when we log in
it fetches some data and that causes our saved request (in VaadinDefaultRequestCache.class) to be for '/'.

### Saving the new user to the database

Start off by adding a database dependency and set up the spring properties, in my case it was postgresql.
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```
```yaml
spring:
  mustache:
    check-template-location: false
  datasource:
    url: 'jdbc:postgresql://localhost:5432/hilla-crm'
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    generate-ddl: true
    hibernate:
      ddl-auto: validate
```
Create the [User.java](src/main/java/com/jeno/application/data/entity/User.java) entity.

We will use our [OAuth2UserService](src/main/java/com/jeno/application/security/oauth2/OAuth2UserService.java) to save a new user to the database.
Existing users can be fetched from db again by using the User#providerId field, this providerId is provided by the oauth2 client (f.e. Google).

Users also have their type of authentication stored, see [AuthProviderType](src/main/java/com/jeno/application/data/entity/AuthProviderType.java) so that we can differentiate between local and oauth users.

## Useful links

- The react-spring-app referenced in this repo is based
  off [this article](https://www.callicoder.com/spring-boot-security-oauth2-social-login-part-1/) so some parts are also used here.
