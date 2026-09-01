```shell
openssl rand -base64 30 | tr '+/' '-_' | tr -d '\n'
```

```shell
gen() { openssl rand -base64 30 | tr '+/' '-_' | tr -d '\n'; }
{
  echo "S3_ADMIN_KEY=$(openssl rand -hex 10)"
  echo "S3_ADMIN_SECRET=$(gen)"
  echo "S3_APP_KEY=$(openssl rand -hex 10)"
  echo "S3_APP_SECRET=$(gen)"
} >> .env
```