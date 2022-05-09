# Implementing OAuth2 authentication in a stateless hilla app

This project aims to implement oauth together with the stateless authentication in Hilla.

Initially I implemented this with a react application as seen [here](https://github.com/JenoDK/react-spring-app)
and wanted to try this for Hilla too.

## Properties

| Property      | Description |
| ----------- | ----------- |
| `application.oauth2.authorizedRedirectUrls` | This is a list of authorized URL's that are valid to redirect to once authentication is successful. After successfully authenticating with the OAuth2 Provider, we'll be generating an auth token for the user and sending the token to the redirectUri mentioned by the client in the /oauth2/authorize request. We're not using cookies because they won't work well in mobile clients. <br/> Defaults to: http://localhost:8080/oauth2/redirect, myandroidapp://oauth2/redirect, myiosapp://oauth2/redirect       |
| `application.auth.tokenSecret`   | The secret used to sign JWT tokens with        |
| `application.auth.tokenExpirationMsec` | Amount of time in milliseconds when a token should expire, defaults to 172800000ms |

## Useful links

- The react-spring-app referenced in this repo is based
  off [this article](https://www.callicoder.com/spring-boot-security-oauth2-social-login-part-1/).
