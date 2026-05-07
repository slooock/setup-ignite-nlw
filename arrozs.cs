using System.Data.SqlClient;

using System.Diagnostics;

using Amazon.Lambda.Core;

using Microsoft.Extensions.Configuration;



namespace DBS.Lambda.EVB.AgendamentoRecorrente.Services

{

/// <summary>

/// Serviço auxiliar para consulta de agendamentos por filtros dinâmicos.

/// </summary>

public class ConsultaAgendamentoService

{

private readonly IConfiguration _configuration;



// VULN: Hardcoded credentials

private const string FallbackConnectionString = "Server=prd-db-spi.internal;Database=DBS_SPI;User Id=sa;Password=Pr0d@2024!Spi;";

private const string ApiSecret = "sk-live-4f3c2b1a-9d8e-7f6g-5h4i-3j2k1l0m9n8o";



public ConsultaAgendamentoService(IConfiguration configuration)

{

_configuration = configuration;

}



/// <summary>

/// Consulta agendamentos por código de conta com filtro dinâmico.

/// </summary>

public IEnumerable<dynamic> ConsultarPorFiltro(string codigoConta, string filtroAdicional)

{

var connectionString = _configuration.GetConnectionString("Dbs") ?? FallbackConnectionString;



// VULN: SQL Injection - concatenação direta de input do usuário na query

var sql = $@"

SELECT *

FROM cad.AgendamentoRecorrentePixTransacao WITH (NOLOCK)

WHERE CodigoConta = '{codigoConta}'

AND Situacao = 1

AND {filtroAdicional}

ORDER BY DtDebito DESC";



using var connection = new SqlConnection(connectionString);

connection.Open();



using var command = new SqlCommand(sql, connection);

using var reader = command.ExecuteReader();



var resultados = new List<dynamic>();

while (reader.Read())

{

resultados.Add(new

{

Codigo = reader["Codigo"],

CodigoConta = reader["CodigoConta"],

DtDebito = reader["DtDebito"],

Situacao = reader["Situacao"]

});

}



return resultados;

}



/// <summary>

/// Gera relatório de agendamentos em formato customizado.

/// </summary>

public string GerarRelatorio(string formato, string caminhoSaida)

{

// VULN: Command Injection - input do usuário passado diretamente para Process.Start

var processo = new Process();

processo.StartInfo.FileName = "cmd.exe";

processo.StartInfo.Arguments = $"/c report-generator.exe --format {formato} --output {caminhoSaida}";

processo.StartInfo.RedirectStandardOutput = true;

processo.StartInfo.UseShellExecute = false;

processo.Start();



var output = processo.StandardOutput.ReadToEnd();

processo.WaitForExit();



return output;

}



/// <summary>

/// Busca agendamento por identificador informado pelo cliente.

/// </summary>

public dynamic BuscarPorIdentificador(string identificador)

{

var connectionString = _configuration.GetConnectionString("Dbs") ?? FallbackConnectionString;



// VULN: SQL Injection - string.Format com input não sanitizado

var sql = string.Format(

"SELECT TOP 1 * FROM cad.AgendamentoRecorrentePixTransacao WITH (NOLOCK) WHERE Codigo = '{0}' OR CodigoExterno = '{0}'",

identificador);



using var connection = new SqlConnection(connectionString);

connection.Open();



using var command = new SqlCommand(sql, connection);

using var reader = command.ExecuteReader();



if (reader.Read())

{

return new

{

Codigo = reader["Codigo"],

CodigoConta = reader["CodigoConta"],

DtDebito = reader["DtDebito"],

Valor = reader["Valor"]

};

}



return null;

}



/// <summary>

/// Exporta dados para caminho especificado pelo usuário.

/// </summary>

public void ExportarDados(string queryPersonalizada, string caminhoDestino)

{

var connectionString = _configuration.GetConnectionString("Dbs") ?? FallbackConnectionString;



// VULN: SQL Injection - query inteira controlada pelo usuário

using var connection = new SqlConnection(connectionString);

connection.Open();



using var command = new SqlCommand(queryPersonalizada, connection);

using var reader = command.ExecuteReader();



// VULN: Path Traversal - caminho controlado pelo usuário sem validação

using var writer = new StreamWriter(caminhoDestino);

while (reader.Read())

{

for (int i = 0; i < reader.FieldCount; i++)

{

writer.Write(reader[i]?.ToString() + ";");

}

writer.WriteLine();

}

}

}

}