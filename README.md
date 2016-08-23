# Scriber

Sql Dosyası: https://drive.google.com/open?id=0B4_U5WAdFcTcSFBmQmhCREJTSms
Soket Dosyası : https://drive.google.com/open?id=0B4_U5WAdFcTcalV3bWxtNW1XT0k
Web Servisleri : https://drive.google.com/open?id=0B4_U5WAdFcTcZFBjVzFicUZpalU

UYGULAMANIN ÇALIŞMASI

Android  ile  geliştirilen  uygulama  twitter  benzeri  bilgi  paylaşımı şeklinde  bir  uygulamadır.  Uygulamada  kullanıcılar  birbirini  
takip edebilmekte, takip edilen kişilerin paylaşımlarını görebilme özelliğine sahiptir.  Kişiler  ile  arasında  konuşma  yapabilen  bir
chat   servisi sağlanmıştır. 
Kişi arama özelliği ile istenilen kişiler takip edilebilir. Ve ya o kişilerin takipçilerini ve postlarını görüntüleyebilir. 
Uygulamamız  teknik  olarak  kullanıcıların  uygulamadan  yaptıkları istekleri  duyan  sokete  sahiptir.  Soket  node.js  
ile  geliştirilmiş  ver uygulama ile soket arasında json formatında bilgi alıp verebilen bir platform  oluşturulmuştur.  
Uygulamanın  isteklerine  göre  soket fonksiyonları çalışarak web servisleri ile ilişki içindedir. 
Web servisleri direk uygulama ile değil soket ile ilişkilidir. Soket restful servis olarak kullandığı servislerden istekte 
bulunmaktadır. Soket ile web servisleri arasında veri akış türü json ile sağlanmaktadır. Web servisleri soket fonksiyonunun 
özelliğine göre çağırılmaktadır.
Web  servisine  gelen  veriler  mysql  veritabanı  ile  bağlantı  kurar  ve servisin özelliğine göre sorgu çalıştırır. 
Veritabanı sadece web servis ile ilişki içindedir. Web servisi bağlantıyı kurar ve sorgusunu çalıştırır. Sorgu sonucunu 
çeviren web servis kendi içinde sorgu sonucunu json formatına  çevirir  ve  soketin  isteğine  karşılık  geriye  json  verisi 
döndürür.
Soket servisten dönen değeri alarak bir üst katmana yani uygulamaya soket fonksiyonunun özelliğine göre yayımlama yapar.
Böylece kullanıcı istediği verileri kayıt edebilir, sorgulayabilir, silebilir ve  görüntüleyebilir.  Bu  şekilde  oluşturulan  
uygulama  kullanıcıya twitter benzeri bir kullanım sunmaktadır.

