# RERA Android — Starter MVP

এটি RERA-র Android native starter project। Android Studio-তে খুলে APK build করা যাবে।

## এখন যা কাজ করে
- Android native app shell (Kotlin + Jetpack Compose)
- বাংলা voice input (ফোনের Speech Recognition service-এর মাধ্যমে)
- বাংলা Text-to-Speech (ডিভাইসে বাংলা TTS voice থাকলে)
- অনুমতি-আগে: প্রতিটি সমর্থিত action-এর আগে confirmation dialog
- অনুমতি দিলে WhatsApp, YouTube, Chrome, Gmail বা Settings খোলা
- নম্বর-সহ call command দিলে শুধু Dialer খোলা; কল নিজে থেকে করা হয় না

## গুরুত্বপূর্ণ সীমাবদ্ধতা
- এটি প্রথম MVP, সম্পূর্ণ AI agent নয়।
- Cloud AI brain / account sync / long-term memory / reminders / advanced automation এখনও যুক্ত নয়।
- Android-এর নিরাপত্তা নীতির কারণে সব অ্যাপ বা সব ফোন সেটিংস নিয়ন্ত্রণ করা যায় না।
- মাইক্রোফোনের জন্য Android Speech Recognition UI ব্যবহার হয়; প্রথমবার ফোনে permission চাইতে পারে।
- বাংলা voice input ও TTS ফোনে উপলব্ধ speech service/voice-এর ওপর নির্ভরশীল।

## APK বানানোর ধাপ
1. কম্পিউটারে Android Studio ইনস্টল করো।
2. Android Studio → Open → এই `RERA_Android_Starter` ফোল্ডারটি নির্বাচন করো।
3. Gradle sync শেষ হতে দাও (প্রথমবার ইন্টারনেট লাগবে)।
4. Build → Build Bundle(s) / APK(s) → Build APK(s)।
5. `app/build/outputs/apk/debug/app-debug.apk` ফাইলটি Android ফোনে পাঠিয়ে install করো।
6. ফোনে “Install unknown apps” অনুমতি চাইলে Android-এর নির্দেশ অনুসরণ করো।

## Build settings
- Application ID: `com.rera.assistant`
- Min Android: 8.0 (API 26)
- Compile SDK: 35
- Version: 0.1.0

## পরবর্তী কাজ
Cloud AI backend যুক্ত করা, secure sign-in, cloud sync, reminder notifications, contacts/calendar integration এবং action-by-action approval policy-এর পূর্ণ implementation।