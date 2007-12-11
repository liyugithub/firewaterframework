package org.firewaterframework.http.security;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.GrantedAuthority;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Aug 2, 2007
 * Time: 6:17:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class FullUser implements UserDetails
{
    protected Map<String,Object> additionalProperties;
    private String password;
    private String username;
    private GrantedAuthority[] authorities;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private Integer userid=null;  // this is the underlying ID (for REST URLs and the DB) for the user

    public FullUser(String username, String password, boolean enabled, boolean accountNonExpired,
        boolean credentialsNonExpired, boolean accountNonLocked, GrantedAuthority[] authorities,
        Map<String,Object> additionalProperties, int userid )
    {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
        this.additionalProperties = additionalProperties;
        this.userid = userid;
    }

    public Map<String, Object> getAdditionalProperties()
    {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties)
    {
        this.additionalProperties = additionalProperties;
    }

    public GrantedAuthority[] getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Userid: ").append(this.userid).append("; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if (this.getAuthorities() != null) {
            sb.append("Granted Authorities: ");

            for (int i = 0; i < this.getAuthorities().length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(this.getAuthorities()[i].toString());
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }

}
