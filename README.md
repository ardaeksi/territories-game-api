# territories-game-api

Backend for a Risk-style territory conquest game. Spring Boot 4, Java 21, Postgres, HTTPS-only.

Also carries the personnel/equipment domain (units, soldier teams, assets) from the project this
was forked out of - kept because the game's planned soldiers/war feature builds on it.

## Local setup

**1. Postgres** (Docker):
```
docker run -d --name readiness-postgres -e POSTGRES_DB=readinessdb -e POSTGRES_USER=readiness -e POSTGRES_PASSWORD=readiness_dev_only -p 5432:5432 postgres:16
```

**2. TLS keystore** (gitignored, generate your own - never commit a real one):
```
keytool -genkeypair -alias readiness -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 365 -storepass "dev-only-changeme" -dname "CN=localhost, OU=Dev, O=TerritoriesGame, L=Fayetteville, ST=NC, C=US"
```

**3. Run:**
```
./mvnw.cmd spring-boot:run
```

**4. Accept the self-signed cert** (once per browser) by visiting `https://localhost:8443/api/units` directly first.

## Running for local network (LAN) play

By default, nothing outside this machine can reach the backend or frontend, even though Spring
Boot binds all interfaces - Windows Firewall blocks unsolicited inbound connections unless a rule
explicitly allows it. Two ports need opening for LAN peers to join a game: **8443** (backend) and
**5173** (frontend dev server, in the `territories-game-web` repo).

These rules are **intentionally not left on by default**. Add them right before a LAN session,
remove them right after - treat it like a toggle, not a standing setting.

**Enable** (run in an elevated/Administrator PowerShell):
```powershell
New-NetFirewallRule -DisplayName "territories-game-api (8443)" -Direction Inbound -Protocol TCP -LocalPort 8443 -Action Allow -Profile Private
New-NetFirewallRule -DisplayName "territories-game-web (5173)" -Direction Inbound -Protocol TCP -LocalPort 5173 -Action Allow -Profile Private
```

**Disable** (run when you're done playing):
```powershell
Remove-NetFirewallRule -DisplayName "territories-game-api (8443)"
Remove-NetFirewallRule -DisplayName "territories-game-web (5173)"
```

Both rules are scoped to the **Private** network profile only (never Public) and to exactly those
two ports - nothing else on this machine is exposed by adding them.

Once enabled:
1. Start the frontend with `npm run dev:lan` instead of `npm run dev` (binds to all network
   interfaces, not just localhost).
2. Find this machine's LAN IP: `ipconfig` (look for the IPv4 address on your actual Wi-Fi/Ethernet
   adapter, not a `vEthernet`/WSL virtual one).
3. Each peer device's browser needs to separately accept the backend's self-signed cert once, by
   visiting `https://<host-LAN-IP>:8443/api/units` directly before joining.
4. Peers then open `http://<host-LAN-IP>:5173` to join the game.
