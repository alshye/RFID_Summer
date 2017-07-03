import processing.serial.*;
Serial mySerial;
PrintWriter output;
int end = 0;

void setup() {
   mySerial = new Serial( this, Serial.list()[0], 9600 );
   output = createWriter( "temperature.txt" );
}
void draw() {
    if (mySerial.available() > 0 ) {
         if(end == 1 ) { output = createWriter( "temperature.txt" ); end = 0; }
         String value = mySerial.readString();
         if (value != null) {
              output.print( value );
              if(value.contains("\n")) { output.close(); end = 1; }
         }
         
         println(value);
         output.flush();
    }
}