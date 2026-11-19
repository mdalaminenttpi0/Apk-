# UltraHD YouTube Downloader APK (Android Kotlin + yt-dlp + FFmpeg)

এই Android অ্যাপ প্রজেক্টটি [youtubedl-android](https://github.com/yausername/youtubedl-android) (যার ভেতরে [yt-dlp](https://github.com/yt-dlp/yt-dlp) ও [FFmpeg](https://www.ffmpeg.org/) বিল্ট-ইন থাকে) ব্যবহার করে সরাসরি আপনার ফোনের **`Download/UltraHD-YouTube`** ফোল্ডারে **8K (4320p), 4K (2160p), 2K (1440p), 1080p** ভিডিও এবং **MP3** অডিও ডাউনলোড ও মার্জ করে।

## কম্পিউটার ছাড়াই মোবাইল + GitHub দিয়ে APK বানানোর ধাপ

1. **GitHub-এ নতুন রিপোজিটরি তৈরি করুন:** মোবাইলের ব্রাউজার থেকে GitHub-এ লগইন করে একটি নতুন Repository তৈরি করুন।
2. **ফাইলগুলো আপলোড করুন:** এই ZIP ফাইলটি আনজিপ করে ভেতরের সব ফাইল (`.github/workflows/build-apk.yml` সহ) আপনার GitHub রিপোজিটরিতে আপলোড বা পুশ করুন।
3. **GitHub Actions থেকে APK ডাউনলোড করুন:**
   * রিপোজিটরির **Actions** ট্যাবে যান।
   * **Build Android APK** ওয়ার্কফ্লোটি চালু হবে (সবুজ টিক চিহ্ন এলে ৩–৫ মিনিটে বিল্ড শেষ হবে)।
   * ওয়ার্কফ্লোটির ভেতরে ঢুকে **Artifacts** সেকশন থেকে **`UltraHD-YouTube-Downloader-APK`** ডাউনলোড করে আনজিপ করুন — ভেতরেই আপনার রেডি **`app-debug.apk`** পেয়ে যাবেন।
4. **ফোনে ইনস্টল ও ব্যবহার:** `app-debug.apk` ফোনে ইনস্টল করে YouTube লিংক পেস্ট করুন, 8K/4K/2K বেছে নিন এবং **ডাউনলোড ও মার্জ শুরু করুন** বাটনে চাপুন।

*(আপনি চাইলে Android Studio-তে প্রজেক্টটি ওপেন করেও সরাসরি Build > Build APK করতে পারবেন।)*
