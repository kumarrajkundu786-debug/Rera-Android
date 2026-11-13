# RERA Android — Multilingual Starter (বাংলা নির্দেশিকা)

এই সংস্করণে RERA-র প্রাথমিক Android অ্যাপ, বাংলা UI, voice input, language selection, ML Kit language identification/translation, text-to-speech এবং অনুমতি-নির্ভর কয়েকটি ফোন অ্যাকশন আছে।

## এই সংস্করণে যা আছে
- Android 8.0 (API 26) বা পরবর্তী সংস্করণ।
- মাইক্রোফোন বোতামে চাপ দিয়ে কথা বলা।
- কথার ভাষা বেছে নেওয়া; না বাছলে ফোনের Speech Recognition provider-এর ডিফল্ট ভাষা ব্যবহৃত হবে। Speech recognition-এর মান ও ভাষা-সমর্থন ফোনের Speech Recognition provider-এর উপর নির্ভর করে। স্বীকৃত টেক্সটের ভাষা ML Kit দিয়ে শনাক্ত করার চেষ্টা হয়।
- অনুবাদের লক্ষ্য ভাষা বেছে নেওয়া। ML Kit Translate-এর ৫০টির বেশি সমর্থিত ভাষার মধ্য থেকে এই সংস্করণে একটি নির্বাচিত তালিকা দেওয়া আছে। Punjabi-সহ সব ভাষা ML Kit Translate-এ নেই।
- প্রথমবার source/target translation model ডাউনলোডে ইন্টারনেট লাগতে পারে; ডাউনলোডের পরে মডেলভিত্তিক অনুবাদ অফলাইনে চলতে পারে।
- TTS ফোনে উপলব্ধ voice ব্যবহার করে; নারী কণ্ঠ নিশ্চিত নয়—ডিভাইসের TTS engine ও ইনস্টল করা voice-এর উপর নির্ভর করে।
- WhatsApp, YouTube, Chrome, Gmail, Settings খোলা এবং নম্বরের Dialer খোলার মতো সীমিত অ্যাকশন। প্রতিটি অ্যাকশনের আগে confirmation dialog আসে।

## যা এখনো নেই
- পূর্ণাঙ্গ AI Brain বা cloud AI API integration নেই। API key অ্যাপের মধ্যে hard-code করা যাবে না; নিরাপদ backend দরকার।
- সব ভাষা সমর্থিত নয়। ML Kit অনুবাদ কেবল তার প্রকাশিত সমর্থিত ভাষাগুলির জন্য; Speech Recognition-এর ভাষা-সমর্থন আলাদা।
- “RERA” wake-word সবসময় ব্যাকগ্রাউন্ডে শোনা এখনো বাস্তবায়িত নয়। প্রথম সংস্করণে অ্যাপের মাইক্রোফোন বোতাম ব্যবহার করতে হবে। Always-on wake word-এর জন্য আলাদা on-device engine, battery/privacy design এবং Android background restrictions সামলাতে হবে।
- সম্পূর্ণ ফোন নিয়ন্ত্রণ সম্ভব নয়। Android sandbox/permission নীতি অনুযায়ী কেবল অনুমোদিত API/intent দিয়ে কাজ করা যাবে।
- এই প্রকল্পটি source code; APK নয়।

## APK তৈরির জন্য
1. Windows/Mac/Linux কম্পিউটারে Android Studio ইনস্টল করো।
2. এই ফোল্ডারটি Android Studio-তে Open করো।
3. Gradle sync সম্পূর্ণ হতে দাও (ইন্টারনেট লাগবে; ML Kit dependencies ডাউনলোড হবে)।
4. Build > Build Bundle(s) / APK(s) > Build APK(s) নির্বাচন করো।
5. Android Studio যে APK path দেখাবে, সেটি ফোনে কপি করে ইনস্টল করো।

শুধু Android ফোনে build করতে চাইলে নির্ভরযোগ্য compatible IDE/build environment আলাদা করে বেছে নিতে হবে। অজানা APK বা terminal command ব্যবহার কোরো না।

## নিরাপত্তা
- RERA কোনো action confirmation ছাড়া চালায় না।
- কলের জন্য শুধু Dialer খোলে; নিজে থেকে কল করে না।
- কোনো Accessibility Service, SMS send, contacts, files বা location permission এই starter-এ যোগ করা হয়নি।
