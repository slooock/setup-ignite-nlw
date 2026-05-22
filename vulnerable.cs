using System;
using System.Diagnostics;
using System.Security.Cryptography;
using System.Data.SqlClient;
using System.Xml;
using System.IO;
using System.DirectoryServices;
using System.Net;
using System.Net.Security;
using System.Security.Authentication;
using Newtonsoft.Json;
using System.Runtime.Serialization.Formatters.Binary;
using System.IO.Compression;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Html;
using Microsoft.AspNetCore.Builder;

namespace VulnerableDotNet
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);
            var app = builder.Build();
            app.MapGet("/", () => "Vulnerable App running...");
            app.Run();
        }
    }

    public class VulnerableController : Controller
    {
        // 1. SQL Injection (SCS0001 / SCS0002 / Semgrep)
        [HttpGet("/sql")]
        public void ExecuteUnsafeSql(string userInput)
        {
            using (var connection = new SqlConnection("Server=myServerAddress;Database=myDataBase;User Id=myUsername;Password=myPassword;"))
            {
                connection.Open();
                // Vulnerable: Direct string concatenation of user input in SQL query
                string query = "SELECT * FROM Users WHERE Username = '" + userInput + "'";
                using (var command = new SqlCommand(query, connection))
                {
                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            Console.WriteLine(reader[0]);
                        }
                    }
                }
            }
        }

        // 2. Command Injection (SCS0005 / Semgrep)
        [HttpGet("/cmd")]
        public void ExecuteUnsafeCommand(string userInput)
        {
            // Vulnerable: User input is passed directly to bash executing a shell command
            var process = new Process();
            process.StartInfo.FileName = "/bin/bash";
            process.StartInfo.Arguments = "-c " + userInput;
            process.Start();
        }

        // 3. Weak Cryptography - MD5 (SCS0006 / Semgrep)
        public byte[] ComputeWeakHashMD5(byte[] data)
        {
            // Vulnerable: MD5 is insecure for password hashing or cryptographic uses
            using (var md5 = MD5.Create())
            {
                return md5.ComputeHash(data);
            }
        }

        // 4. Weak Cryptography - SHA1 (SCS0006 / Semgrep)
        public byte[] ComputeWeakHashSHA1(byte[] data)
        {
            // Vulnerable: SHA1 is a weak cryptographic primitive
            using (var sha = SHA1.Create())
            {
                return sha.ComputeHash(data);
            }
        }

        // 5. Weak Random Number Generator (SCS0017 / Semgrep)
        public int GenerateUnsafeRandom()
        {
            // Vulnerable: System.Random is not cryptographically secure
            var random = new Random();
            return random.Next();
        }

        // 6. Hardcoded Secrets / Credentials (SCS0015 / SCS0016 / Semgrep)
        private const string SlackApiKey = "xoxb-123456789012-abcdefghijklmnopqrstuvwx";
        private const string SuperSecurePassword = "MySuperSecretPassword123!";

        // 7. Path Traversal / Path Injection (SCS0018 / Semgrep)
        [HttpGet("/path")]
        public string ReadUserFile(string userInput)
        {
            // Vulnerable: Concatenating input to base path allows path traversal (e.g. "../../../etc/passwd")
            string basePath = "/var/www/uploads/";
            string filePath = Path.Combine(basePath, userInput);
            return File.ReadAllText(filePath);
        }

        // 8. Insecure XML External Entity Parsing (SCS0007 / XXE / Semgrep)
        [HttpPost("/xml")]
        public void ParseXmlUnsafely(string xmlContent)
        {
            // Vulnerable: XmlDocument with insecure resolver and DTD processing enabled
            var doc = new XmlDocument();
            var settings = new XmlReaderSettings();
            settings.DtdProcessing = DtdProcessing.Parse;
            settings.XmlResolver = new XmlUrlResolver(); // allows loading external XML resources / schemas

            using (var reader = XmlReader.Create(new StringReader(xmlContent), settings))
            {
                doc.Load(reader);
            }
        }

        // 9. LDAP Injection (SCS0004 / Semgrep)
        public void UnsafeLdapSearch(string userInput)
        {
            using (var entry = new DirectoryEntry("LDAP://OU=Users,DC=example,DC=com"))
            {
                // Vulnerable: Direct string concatenation in LDAP filter
                string filter = "(&(objectClass=user)(sAMAccountName=" + userInput + "))";
                using (var searcher = new DirectorySearcher(entry))
                {
                    searcher.Filter = filter;
                    var results = searcher.FindAll();
                    foreach (SearchResult result in results)
                    {
                        Console.WriteLine(result.Path);
                    }
                }
            }
        }

        // 10. Disabled Certificate Validation (SCS0003 / Semgrep)
        public void DisableSslValidation()
        {
            // Vulnerable: Trusts all SSL/TLS certificates unconditionally, opening up MITM attacks
            ServicePointManager.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) => true;
        }

        // 11. Weak Symmetric Encryption Algorithm - DES & RC2 (SCS0010 / Semgrep)
        public byte[] EncryptWithDes(byte[] data, byte[] key, byte[] iv)
        {
            // Vulnerable: DES uses a weak 56-bit key size
            using (var des = DES.Create())
            {
                des.Key = key;
                des.IV = iv;
                using (var encryptor = des.CreateEncryptor())
                {
                    return encryptor.TransformFinalBlock(data, 0, data.Length);
                }
            }
        }

        public byte[] EncryptWithRc2(byte[] data, byte[] key, byte[] iv)
        {
            // Vulnerable: RC2 is a weak legacy symmetric cipher
            using (var rc2 = RC2.Create())
            {
                rc2.Key = key;
                rc2.IV = iv;
                using (var encryptor = rc2.CreateEncryptor())
                {
                    return encryptor.TransformFinalBlock(data, 0, data.Length);
                }
            }
        }

        // 12. Insecure Deserialization - BinaryFormatter (SCS0028 / Semgrep)
        public object DeserializeBinary(byte[] payload)
        {
            // Vulnerable: BinaryFormatter is inherently unsafe for untrusted input
            var formatter = new BinaryFormatter();
            using (var stream = new MemoryStream(payload))
            {
                #pragma warning disable SYSLIB0011 // BinaryFormatter is obsolete
                return formatter.Deserialize(stream);
                #pragma warning restore SYSLIB0011
            }
        }

        // 13. Insecure Deserialization - Newtonsoft.Json (Semgrep)
        public object DeserializeJsonUnsafely(string jsonPayload)
        {
            // Vulnerable: TypeNameHandling.All/Auto allows arbitrary code execution during deserialization
            var settings = new JsonSerializerSettings
            {
                TypeNameHandling = TypeNameHandling.All
            };
            return JsonConvert.DeserializeObject(jsonPayload, settings);
        }

        // 14. Hardcoded Cryptographic Key & IV (SCS0011 / SCS0015 / Semgrep)
        public byte[] EncryptWithHardcodedKey(byte[] data)
        {
            using (var aes = Aes.Create())
            {
                // Vulnerable: Hardcoded key and IV compromise the security of encryption
                aes.Key = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16 };
                aes.IV = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16 };
                using (var encryptor = aes.CreateEncryptor())
                {
                    return encryptor.TransformFinalBlock(data, 0, data.Length);
                }
            }
        }

        // 15. Zip Slip / Unsafe Zip Extraction (Semgrep)
        public void ExtractZipUnsafely(string zipPath, string targetDirectory)
        {
            using (var archive = ZipFile.OpenRead(zipPath))
            {
                foreach (var entry in archive.Entries)
                {
                    // Vulnerable: Path.Combine directly matches the entry path, allowing Directory Traversal ("../")
                    string destinationPath = Path.Combine(targetDirectory, entry.FullName);
                    
                    // A safe implementation would check if destinationPath.StartsWith(targetDirectory)
                    entry.ExtractToFile(destinationPath, true);
                }
            }
        }

        // 16. Weak SSL/TLS Protocol Selection (SCS0008 / Semgrep)
        public void UseWeakSslProtocols()
        {
            // Vulnerable: Enabling obsolete/broken SSLv3, TLS 1.0, and TLS 1.1 protocols
            #pragma warning disable CS0618
            ServicePointManager.SecurityProtocol = SecurityProtocolType.Ssl3 | SecurityProtocolType.Tls | SecurityProtocolType.Tls11;
            #pragma warning restore CS0618
        }

        // --- ASP.NET Web / MVC Specific Vulnerabilities (SCS0009 / SCS0026 / SCS0027 / SCS0029) ---

        // 17. Cross-Site Scripting - XSS (SCS0029)
        [HttpGet("/xss")]
        public ContentResult GetXss(string name)
        {
            // Vulnerable: Returning raw HTML with unescaped user input
            return Content("<html><body><h1>Hello, " + name + "</h1></body></html>", "text/html");
        }

        [HttpGet("/htmlstring")]
        public HtmlString GetHtmlString(string input)
        {
            // Vulnerable: Raw HTMLString bypassing HTML sanitization/escaping
            return new HtmlString("<div>" + input + "</div>");
        }

        // 18. Open Redirect (SCS0027)
        [HttpGet("/redirect")]
        public IActionResult UnsafeRedirect(string url)
        {
            // Vulnerable: Redirecting to user-provided URL without validating if it is local/safe
            return Redirect(url);
        }

        // 19. Cross-Site Request Forgery - CSRF / Missing AntiForgery Token (SCS0016 / SCS0020)
        [HttpPost("/update-profile")]
        // Vulnerable: Missing [ValidateAntiForgeryToken] attribute on state-changing POST action
        public IActionResult UpdateProfile(string email)
        {
            Console.WriteLine("Profile updated to: " + email);
            return Ok();
        }

        // 20. Insecure Cookie Policy (SCS0009 / SCS0025)
        [HttpGet("/cookie")]
        public void SetInsecureCookie()
        {
            // Vulnerable: HttpOnly and Secure flags are set to false or omitted
            var options = new CookieOptions
            {
                HttpOnly = false,
                Secure = false
            };
            Response.Cookies.Append("SessionToken", "SuperSecretSessionValue", options);
        }
    }
}
