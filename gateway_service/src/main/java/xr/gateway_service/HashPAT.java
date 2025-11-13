package xr.gateway_service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashPAT {
    public static void main(String[] args) {
        var enc = new BCryptPasswordEncoder();
        System.out.println(enc.encode("MY_SUPER_BOOTSTRAP_PAT_48CHARS_1234567890abcd"));
    }
}
