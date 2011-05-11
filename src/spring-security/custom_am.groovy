@Grab('org.springframework.security:spring-security-core:3.0.5.RELEASE')
import org.springframework.security.authentication.*
import org.springframework.security.core.*
import org.springframework.security.core.authority.*
import org.springframework.security.core.context.SecurityContextHolder

def console = System.console()
def username = console.readLine('Your username: > ') as String
def password = console.readPassword('\nYour password: > ') as String

println "$username and $password given"

AuthenticationManager am = new AuthenticationManager() {
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
      println "auth.name is ${auth.name}, auth.credentials is ${auth.credentials}"
    
      if (auth.getName().equals(auth.getCredentials())) {
        return new UsernamePasswordAuthenticationToken(auth.getName(),
          auth.getCredentials(), [new GrantedAuthorityImpl('ROLE_USER')]);
        }
        throw new BadCredentialsException("Bad Credentials");
    }
}

Authentication result = am.authenticate(new UsernamePasswordAuthenticationToken(username, password))
assert result != null
