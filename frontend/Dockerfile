# Use an Nginx base image
FROM nginx:alpine

# Copy your static files into the Nginx web root
COPY src/html /usr/share/nginx/html
COPY src/css /usr/share/nginx/html/css
COPY src/js /usr/share/nginx/html/js

# Expose the default Nginx HTTP port
EXPOSE 80

# Start Nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]
