// package com.todolist.ToDoList.config;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.todolist.ToDoList.service.JwtService;
// import com.todolist.ToDoList.service.UserService;

// import java.io.IOException;

// @Component
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     private final JwtService jwtService;
//     private final UserService userService;

//     @Autowired
//     public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
//         this.jwtService = jwtService;
//         this.userService = userService;
//     }

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {

//         System.out.println("Request URL: " + request.getRequestURL());
//         System.out.println("Request Method: " + request.getMethod());
//         System.out.println("Authorization Header: " + request.getHeader("Authorization"));

//         final String authHeader = request.getHeader("Authorization");
//         String jwt = null;
//         String email = null;

//         if (authHeader != null && authHeader.startsWith("Bearer ")) {
//             System.out.println("JWT found in Authorization header");
//             jwt = authHeader.substring(7); // Extract the JWT
//             System.out.println("JWT: " + jwt);
//             email = jwtService.extractEmail(jwt); // Extract the email from the token
//             System.out.println("Email: " + email);

//         }

//         if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//             UserDetails userDetails = userService.loadUserByEmail(email);

//             if (jwtService.validateJwt(jwt)) {
//                 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                         userDetails, null, userDetails.getAuthorities());
//                 SecurityContextHolder.getContext().setAuthentication(authToken);
//             }
//         }

//         filterChain.doFilter(request, response);
//     }
// }
