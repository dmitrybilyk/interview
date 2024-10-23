# Java I/O Streams

## What are I/O Streams?
I/O (Input/Output) streams in Java allow for reading and writing data.

### Types of Streams
1. **Byte Streams**: Handle raw binary data (8-bit bytes).
    - Classes: `InputStream`, `OutputStream`.
2. **Character Streams**: Handle character data (16-bit Unicode).
    - Classes: `Reader`, `Writer`.

### Byte Streams
- **InputStream**: Reads byte data.
- **OutputStream**: Writes byte data.

#### Common Byte Stream Classes
- `FileInputStream`: Reads from a file.
- `FileOutputStream`: Writes to a file.
- `BufferedInputStream`: Buffers input for efficiency.
- `BufferedOutputStream`: Buffers output for efficiency.

### Character Streams
- **Reader**: Reads character data.
- **Writer**: Writes character data.

#### Common Character Stream Classes
- `FileReader`: Reads from a file.
- `FileWriter`: Writes to a file.
- `BufferedReader`: Buffers character input for efficiency.
- `BufferedWriter`: Buffers character output for efficiency.

# It's possible to use byte streams for text processing, but it's not good
# as you need to do encoding/decoding manually then.
## encoding defines how characters are mapped to sequences of bytes


### When You Need Encoding:
`File I/O`: Storing and reading text files.
`Web Development`: Displaying international characters on web pages.
`Data Transmission`: Sending/receiving text over networks (emails, APIs).
`Multilingual Support`: Handling various languages and special characters.
`Data Interchange Formats`: Using JSON, XML for cross-system communication.

Example: Encoding the String "A" in Different Encodings:
In ASCII:
"A" is represented as 65 in decimal, which is 01000001 in binary (7 bits).
In UTF-8:
"A" is also represented as a single byte: 01000001.
In UTF-16:
"A" is represented as 00000000 01000001 (2 bytes).