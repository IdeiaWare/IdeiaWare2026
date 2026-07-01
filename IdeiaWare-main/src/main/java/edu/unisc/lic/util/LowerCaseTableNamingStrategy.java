package edu.unisc.lic.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * CASE-FIX (2026-06-23): forca o nome FISICO da TABELA para minusculo.
 *
 * Motivo: no MySQL do Windows (lower_case_table_names=1) as tabelas sao gravadas em
 * minusculo no disco, mas varias entidades mapeavam para nomes com maiuscula -- classes
 * sem @Table herdam o nome da CLASSE (ex.: Ideia -> "Ideia", Usuario -> "Usuario") e
 * a ExportFile usava @Table("Export_File"). No Windows funciona (case-insensitive); no
 * MySQL do Linux (case-sensitive, default lower_case_table_names=0) daria
 * "Table 'lic_bd.Ideia' doesn't exist" -- e com hbm2ddl=update ainda criaria tabela
 * duplicada. Esta estrategia minuscula SO o nome da tabela; colunas/schema ficam
 * intactos (e no MySQL nomes de coluna ja sao case-insensitive). Resultado: bate com
 * as tabelas reais (canva, ideia, usuario, export_file, ...) em qualquer SO.
 */
public class LowerCaseTableNamingStrategy extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = 1L;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		if (name == null) {
			return null;
		}
		return Identifier.toIdentifier(name.getText().toLowerCase(), name.isQuoted());
	}
}
