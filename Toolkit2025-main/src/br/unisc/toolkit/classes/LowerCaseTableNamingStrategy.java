package br.unisc.toolkit.classes;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * CASE-FIX (2026-06-23): forca o nome FISICO da TABELA para minusculo.
 *
 * Motivo: no MySQL do Windows (lower_case_table_names=1) as tabelas ficam minusculas no
 * disco, mas as entidades Ideia/Usuario (sem @Table) herdam o nome da CLASSE e a
 * ExportFile usava @Table("Export_File"). No Windows funciona (case-insensitive); no
 * MySQL do Linux (case-sensitive) daria "Table 'lic_bd.Ideia' doesn't exist". Esta
 * estrategia minuscula SO o nome da tabela; colunas/schema ficam intactos. Bate com as
 * tabelas reais minusculas (compartilhadas com o LIC) em qualquer SO.
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
