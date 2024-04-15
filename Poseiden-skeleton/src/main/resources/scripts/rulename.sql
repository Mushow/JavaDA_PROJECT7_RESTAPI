ALTER TABLE rule_name AUTO_INCREMENT = 1;
TRUNCATE TABLE rule_name;

insert into rule_name(name, description, json, template, sql_str, sql_part) values ("Name #1", "Description #1", "Json #1", "Template #1", "Sql Str #1", "Sql Part #1"), ("Name #2", "Description #2", "Json #2", "Template #2", "Sql Str #2", "Sql Part #2");