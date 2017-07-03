const int sensorPin = A0;
const float baselineTemp = 20.0;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  for(int pinNumber = 2; pinNumber < 5; pinNumber++) {
    pinMode(pinNumber, OUTPUT);
    digitalWrite(pinNumber, LOW);
  }
}


void loop() {
  // put your main code here, to run repeatedly:
  int sensorVal = analogRead(sensorPin);
  float voltage = (sensorVal/1024.0)* 5.0;
  float temperature = ((voltage - .5) * 100) * (9/5.0) + 32;
  Serial.println(temperature);
  delay(60000);
}
