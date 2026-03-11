FROM jboss/wildfly:10.1.0.Final

USER root

# Download MySQL Connector/J 8.0
RUN curl -fSL https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar \
    -o /tmp/mysql-connector-java-8.0.28.jar

# Install MySQL JDBC driver as a WildFly module
RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/com/mysql/main && \
    cp /tmp/mysql-connector-java-8.0.28.jar \
       /opt/jboss/wildfly/modules/system/layers/base/com/mysql/main/

COPY module.xml /opt/jboss/wildfly/modules/system/layers/base/com/mysql/main/module.xml

# Configure datasource via CLI in embedded mode
COPY configure-datasource.cli /tmp/configure-datasource.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configure-datasource.cli

RUN chown -R jboss:jboss /opt/jboss/wildfly/

USER jboss

EXPOSE 8080 9990

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
