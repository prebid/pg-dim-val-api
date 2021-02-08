
# Full list of application configuration options

This document describes all the configuration properties available for the PG Dimension Value API.


## Server HTTP Port
- `server.port` - server HTTP listener port


## Database related
- `spring.datasource.url` - JDBC URL to application database
- `spring.datasource.username` - database application user 
- `spring.datasource.password` - database application user password
- `spring.datasource.hikari.connection-test-query` - database connection pool test query
- `spring.datasource.hikari.minimum-idle` - minimum number of idle connections maintained in a connection pool
- `spring.datasource.hikari.maximum-pool-size` - maximum connection pool size
- `spring.datasource.hikari.pool-name` - connection pool name
- `spring.datasource.hikari.auto-commit` - default auto-commit behavior
- `spring.jpa.show-sql` - whether to enable logging of SQL statements
- `spring.jpa.properties.hibernate.generate_statistics` - whether to enable Hibernate metrics in log
- `spring.jpa.properties.hibernate.jdbc.batch_size` - controls the maximum number of statements Hibernate will batch together before asking the driver to execute the batch


## JSON related
- `spring.jackson.date-format` - default format for dates in the application
- `spring.jackson.default-property-inclusion` - controls the inclusion of properties during serialization.


## Web related
- `services.base-url` - base service request mapping URL path
- `spring.data.web.pageable.default-page-size` - default size parameter for paging
- `spring.servlet.multipart.max-file-size` - max size of file that can be uploaded
- `spring.servlet.multipart.max-request-size` - max total request size for a multipart/form-data


## Application Data Configuration
- `services.attr-tree-links[i].parent` - parent attribute name pointing to leaf attribute name below
- `services.attr-tree-links[i].leaf` - leaf attribute name


## Server Authentication
- `server-auth.authentication-enabled` - boolean flag to enable authentication
- `server-auth.realm` - scope of service protection
- `server-auth.principals[i].username` - username
- `server-auth.principals[i].password` - password
- `server-auth.principals[i].roles` - comma separated roles assigned to this user
- `server-api-roles.upload` - role allowing upload actions
- `server-api-roles.query` - role allowing query


## Misc
- `management.*` - as defined in [SpringBoot Actuator properties]https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#actuator-properties
- `services.cors.*` - CORS settings as defined in [SpringBoot Actuator properties]https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html#actuator-properties
