// Runs the SQL from the Kotlin sources against SQLite without an Android SDK.
// Requires Node >= 22.13. Does not replace Room/KSP or Android tests.
const fs = require('node:fs');
const path = require('node:path');
const assert = require('node:assert/strict');
const { DatabaseSync } = require('node:sqlite');
const root = path.resolve(__dirname, '../..');
const sourceRoot = path.join(root, 'app/src/main/java/br/com/banavita/app');
const read = p => fs.readFileSync(path.join(sourceRoot, p), 'utf8');
const db = new DatabaseSync(':memory:');
db.exec('PRAGMA foreign_keys = ON');
const schema = JSON.parse(fs.readFileSync(path.join(root, 'app/schemas/br.com.banavita.app.data.local.BanavitaDatabase/3.json'))).database;
for (const entity of schema.entities) {
  db.exec(entity.createSql.replaceAll('${TABLE_NAME}', entity.tableName));
  for (const index of entity.indices) db.exec(index.createSql.replaceAll('${TABLE_NAME}', entity.tableName));
}
for (const view of schema.views) db.exec(view.createSql.replaceAll('${VIEW_NAME}', view.viewName));
const migrations = read('data/local/BanavitaMigrations.kt');
function migrate(from, to) {
  const start = migrations.indexOf(`val MIGRATION_${from}_${to} =`);
  assert(start >= 0);
  const end = migrations.indexOf('\nval MIGRATION_', start + 1);
  const block = migrations.slice(start, end < 0 ? undefined : end);
  const statements = [...block.matchAll(/db\.execSQL\(\s*(?:"""([\s\S]*?)"""\.trimIndent\(\)|"((?:\\.|[^"\\])*)")/g)];
  assert(statements.length > 0);
  for (const match of statements) db.exec(match[1] || JSON.parse(`"${match[2]}"`));
}
for (let version = 3; version < 8; version++) migrate(version, version + 1);
let seedId = 0;
function insert(table, fields) {
  const row = {};
  for (const col of db.prepare(`PRAGMA table_info(${table})`).all()) {
    if (Object.hasOwn(fields, col.name)) row[col.name] = fields[col.name];
    else if (col.notnull && col.dflt_value == null) row[col.name] = col.type === 'TEXT' ? `seed-${++seedId}` : 0;
  }
  db.prepare(`INSERT INTO ${table} (${Object.keys(row).join(',')}) VALUES (${Object.keys(row).map(() => '?').join(',')})`).run(...Object.values(row));
}
insert('produto', { PRO_ID: 'p1', PRO_DESCRICAO: 'Prata', PRO_UNIDADE: 'KG', PRO_PERECIVEL: 1 });
insert('produto', { PRO_ID: 'p2', PRO_DESCRICAO: 'Nanica', PRO_UNIDADE: 'UN' });
insert('produto', { PRO_ID: 'p3', PRO_DESCRICAO: 'Sem custo', PRO_UNIDADE: 'KG' });
insert('cliente', { CLI_ID: 'c1', CLI_NOME: 'Cliente A' });
insert('fornecedor', { FOR_ID: 'f1', FOR_NOME: 'Fornecedor A' });
insert('evento', { EVE_ID: 'event1', EVE_TITULO: 'Entrega antiga', EVE_DATAHORA: 100 });
insert('venda', { VEN_ID: 'old', VEN_CLIENTE: 'c1', VEN_DATA: 100, VEN_VLRTOT: 1000, VEN_PRAZO: 1 });
insert('encomenda', { ENC_ID: 'order1', ENC_CLIENTE: 'c1', ENC_DATA_ENTREGA: 100, ENC_STATUS: 'ATENDIDO', ENC_VENDA: 'old', ENC_EVENTO: 'event1' });
insert('encomenda_item', { ENI_ID: 'orderitem1', ENI_ENCOMENDA: 'order1', ENI_PRODUTO: 'p1', ENI_ESTADO: 'VERDE', ENI_QTDE: 5 });
insert('lote', { LOT_ID: 'lot1', LOT_PRODUTO: 'p1', LOT_CAMARA: 'FRIA', LOT_ESTADO: 'VERDE', LOT_QTDE_RESTANTE: 10, LOT_ETAPA_INICIO_DAY: 123 });
insert('lote', { LOT_ID: 'raw1', LOT_PRODUTO: 'p1', LOT_CAMARA: 'FRIA', LOT_ESTADO: 'CRUA', LOT_QTDE_RESTANTE: 7, LOT_ETAPA_INICIO_DAY: null });
migrate(8, 9);
assert.equal(db.prepare('SELECT ENC_STATUS FROM encomenda').get().ENC_STATUS, 'ENTREGUE');
assert.equal(db.prepare('SELECT ENI_ENTREGUE FROM encomenda_item').get().ENI_ENTREGUE, 5);
assert.equal(db.prepare('SELECT EVE_STATUS FROM evento').get().EVE_STATUS, 'CONCLUIDO');
assert.equal(db.prepare('SELECT VEN_VENCIMENTO FROM venda').get().VEN_VENCIMENTO, null);
assert.equal(db.prepare("SELECT LOT_ESTADO FROM lote WHERE LOT_ID='lot1'").get().LOT_ESTADO, 'VERDE');
assert.equal(db.prepare("SELECT LOT_ETAPA_INICIO_DAY FROM lote WHERE LOT_ID='raw1'").get().LOT_ETAPA_INICIO_DAY, null);
assert(db.prepare("SELECT LOT_ETAPA_INICIO_DAY FROM lote WHERE LOT_ID='lot1'").get().LOT_ETAPA_INICIO_DAY > 20000);
console.log('Migration 3 → 9: data, legacy deliveries, undated titles and ripening clocks preserved');

insert('compra', { COM_ID: 'buy1', COM_FORNECEDOR: 'f1', COM_DATA: 100, COM_VLRTOT: 400 });
insert('compra_item', { COI_ID: 'bi1', COI_COMPRA: 'buy1', COI_PRODUTO: 'p1', COI_UNIDADE: 'KG', COI_QTDE: 10, COI_VLRTOT: 400 });
insert('compra_item', { COI_ID: 'bi2', COI_COMPRA: 'buy1', COI_PRODUTO: 'p2', COI_UNIDADE: 'UN', COI_QTDE: 4, COI_VLRTOT: 100 });
insert('compra', { COM_ID: 'future', COM_DATA: 1000, COM_VLRTOT: 10000 });
insert('compra_item', { COI_ID: 'futureitem', COI_COMPRA: 'future', COI_PRODUTO: 'p1', COI_UNIDADE: 'KG', COI_QTDE: 10, COI_VLRTOT: 10000 });
insert('venda', { VEN_ID: 'sale1', VEN_CLIENTE: 'c1', VEN_DATA: 200, VEN_VLRTOT: 700, VEN_PRAZO: 1, VEN_VENCIMENTO: 500 });
insert('venda_item', { VEI_ID: 'si1', VEI_VENDA: 'sale1', VEI_PRODUTO: 'p1', VEI_UNIDADE: 'KG', VEI_QTDE: 5, VEI_VLRTOT: 500 });
insert('venda_item', { VEI_ID: 'si2', VEI_VENDA: 'sale1', VEI_PRODUTO: 'p3', VEI_UNIDADE: 'KG', VEI_QTDE: 2, VEI_VLRTOT: 200 });
insert('recebimento', { REC_ID: 'receipt1', REC_VENDA: 'sale1', REC_DATA: 300, REC_VALOR: 200 });
insert('pagamento', { PAG_ID: 'payment1', PAG_COMPRA: 'buy1', PAG_DATA: 400, PAG_VALOR: 100 });

const dao = read('data/local/dao/DashboardDao.kt');
let checked = 0;
for (const match of dao.matchAll(/@Query\(\s*(?:"""([\s\S]*?)"""|"((?:\\.|[^"\\])*)")\)\s*(?:suspend\s+)?fun\s+(\w+)/g)) {
  const query = match[1] || JSON.parse(`"${match[2]}"`);
  const bind = {};
  for (const param of query.matchAll(/:(\w+)/g)) bind[param[1]] = ({ start: 0, end: 10000, backlog: 1, id: 'event1', status: 'PENDENTE', now: 500, day: 1, requestId: 'request1' })[param[1]] ?? 0;
  const statement = db.prepare(query);
  const args = Object.keys(bind).length ? [bind] : [];
  const rows = /^SELECT/i.test(query.trim()) ? statement.all(...args) : statement.run(...args);
  if (match[3] === 'finance') assert.equal(rows.find(r => r.id === 'sale1').balance, 500);
  if (match[3] === 'cash') assert.deepEqual(rows.map(r => r.value).sort((a, b) => a - b), [-100, 200]);
  if (match[3] === 'inventory') assert(rows.some(r => r.productId === 'p2' && r.unit === 'UN' && r.quantity === 4));
  checked++;
}
const repo = read('data/repository/DashboardRepository.kt');
const baseTemplate = repo.match(/val base = """([\s\S]*?)"""/)[1];
const sqlTemplate = repo.match(/val sql = """([\s\S]*?)"""/)[1];
const costTemplate = repo.match(/val unitCost = if \(sales\) """([\s\S]*?)"""/)[1];
for (const sales of [true, false]) for (const group of ['total', 'day', 'partner', 'product', 'operation', 'unit']) {
  const key = ({ day: "date(date / 1000, 'unixepoch', 'localtime')", partner: 'partnerId', product: 'productId', operation: 'docId', unit: 'unit', total: "'total'" })[group];
  const values = { table: sales ? 'venda' : 'compra', doc: sales ? 'VEN' : 'COM', item: sales ? 'VEI' : 'COI', partnerId: sales ? 'VEN_CLIENTE' : 'COM_FORNECEDOR', partnerTable: sales ? 'cliente' : 'fornecedor', partnerKey: sales ? 'CLI' : 'FOR', itemDoc: sales ? 'VEI_VENDA' : 'COI_COMPRA', key, label: ['partner', 'operation'].includes(group) ? 'partner' : group === 'product' ? 'product' : key, unitCost: sales ? costTemplate : 'i.COI_VLRTOT * 1.0 / NULLIF(i.COI_QTDE, 0)' };
  function interpolate(template) { return template.replace(/\$\{(\w+)\}|\$(\w+)/g, (all, a, b) => values[a || b] ?? all); }
  values.base = interpolate(baseTemplate.replace('${conditions.joinToString(" AND ")}', `d.${values.doc}_DATA >= ? AND d.${values.doc}_DATA < ?`));
  const sql = interpolate(sqlTemplate.replace('${if (group == "total") "" else "GROUP BY $key"}', group === 'total' ? '' : `GROUP BY ${key}`));
  const rows = db.prepare(sql).all(150, 250);
  if (sales && group === 'total') {
    assert.equal(rows[0].value, 700);
    assert.equal(rows[0].operations, 1);
    assert.equal(rows[0].coveredRevenue, 500);
    assert.equal(rows[0].cost, 200); // Future expensive purchase must not rewrite the estimate.
  }
  if (!sales && group === 'total') assert.equal(rows[0].value, 0);
  checked++;
}
insert('entrega_item', { requestId: 'request1', itemId: 'orderitem1', vendaId: 'sale1', quantidade: 2, data: 200 });
assert.throws(() => insert('entrega_item', { requestId: 'request1', itemId: 'orderitem1', vendaId: 'sale1', quantidade: 2, data: 200 }));
assert.throws(() => db.prepare("DELETE FROM venda WHERE VEN_ID = 'sale1'").run());
assert.deepEqual(db.prepare('PRAGMA foreign_key_check').all(), []);
console.log(`SQLite: ${checked} source queries validated; costs, units, partial balances, duplicate and FK protection passed`);
db.close();
