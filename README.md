# AutoCall Android App — Build APK via GitHub Actions

Follow these steps to get your APK without installing Android Studio.

---

## Step 1 — Create a GitHub account
Go to https://github.com and sign up (free).

---

## Step 2 — Create a new repository
1. Click the **+** icon → **New repository**
2. Name it `autocall-app`
3. Set it to **Private**
4. Click **Create repository**

---

## Step 3 — Upload the project files
On the repository page, click **uploading an existing file** and upload
everything from this folder (all files and the folders: `app/`, `.github/`,
`gradle/`).

Or use Git on your computer:
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/autocall-app.git
git push -u origin main
```

---

## Step 4 — Watch the build
1. Go to your repo on GitHub
2. Click the **Actions** tab
3. You'll see **Build AutoCall APK** running — wait ~3–5 minutes

---

## Step 5 — Download the APK
1. Click on the completed workflow run
2. Scroll down to **Artifacts**
3. Click **autocall-debug** to download the APK zip
4. Extract it — inside is `app-debug.apk`

---

## Step 6 — Install on your phone
1. On your Android phone, go to **Settings → Security** and enable
   **Install from unknown sources** (or "Install unknown apps")
2. Transfer the APK to your phone (via WhatsApp, Google Drive, USB, etc.)
3. Tap the APK file to install

---

## Note
This builds a **debug APK** — perfectly fine for personal use.
If you need a release/signed APK for the Play Store, let me know.
