#include <ESP8266WiFi.h>

const char* ssid = "M4C-622";
const char* password = "Mei4C622";

WiFiServer server(8896);
WiFiClient client;

void setup() {
  Serial.begin(115200);
  delay(10);

  // prepare GPIO2
  pinMode(2, OUTPUT);
  digitalWrite(2, 0);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");

  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.println(WiFi.localIP());
}

void loop()
{
  if (!client.connected()) {
    // try to connect to a new client
    client = server.available();
    Serial.println("New Client Availiable!");
  } else {
    // read data from the connected client
    if (client.available() > 0) {
      String in = client.readStringUntil('\r');
      int num = in.toInt();
      analogWrite(2, num);
      Serial.println(num, DEC);
      client.print("OK\r\n");
    }
  }
}
