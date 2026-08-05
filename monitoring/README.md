# Monitoring stack

The dialog stack includes:

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000, admin/admin
- Zabbix UI: http://localhost:8088

Start from the project root:

```powershell
docker compose -f dialog/docker-compose.yml up -d --build
```

Prometheus scrapes the dialog service at:

```text
http://dialog:8080/actuator/prometheus
```

Grafana provisioning automatically adds the Prometheus datasource and the
`Dialog Service RED` dashboard.

## Zabbix host

After Zabbix Web is ready, create a host:

- Host name: `dialog-service`
- Agent interface: `zabbix-agent`, port `10050`
- Template: `Linux by Zabbix agent`

For an application availability check, add an HTTP check for:

```text
http://dialog:8080/actuator/health
```
