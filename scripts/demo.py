"""Demonstração HTTP opcional: Python 3, somente biblioteca padrão.
Inicie o JAR em outro terminal antes de executar este arquivo.
Cada execução cria novos registros fictícios no banco da API indicada.
"""
import json
import sys
import urllib.error
import urllib.request
import uuid

BASE = (sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8081").rstrip("/")
# O demo deve usar acesso local/túnel de laboratório, sem proxy intermediário.
http = urllib.request.build_opener(urllib.request.ProxyHandler({}))


def call(method, path, payload=None, expected=200):
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    request = urllib.request.Request(BASE + path, data=data, method=method,
                                     headers={"Content-Type": "application/json"})
    try:
        response = http.open(request, timeout=10)
    except urllib.error.HTTPError as error:
        response = error
    with response:
        status = response.code
        result = json.loads(response.read().decode("utf-8"))
    print(f"{method} {path} -> {status}")
    if status != expected:
        raise RuntimeError(f"Esperado {expected}, recebido {status}: {result}")
    return result


def check(condition, message):
    if not condition:
        raise RuntimeError(message)


def main():
    ticket = call("POST", "/api/tickets", {
        "title": "VPN de laboratorio " + uuid.uuid4().hex[:6],
        "description": "Chamado ficticio para demonstracao", "priority": "HIGH"
    }, 201)
    path = f"/api/tickets/{ticket['id']}"
    call("PATCH", path + "/status", {"status": "CLOSED", "note": "Tentativa de pular etapas"}, 409)
    for status in ("IN_PROGRESS", "RESOLVED", "CLOSED"):
        changed = call("PATCH", path + "/status", {"status": status, "note": "Etapa de laboratorio"})
        check(changed["status"] == status, "Status divergente")
    call("PATCH", path + "/status", {"status": "IN_PROGRESS", "note": "Tentativa de reabrir fechado"}, 409)
    history = call("GET", path + "/history")
    check(len(history) == 4, "Historico deve ter criacao e tres transicoes")
    print(json.dumps(history, indent=2, ensure_ascii=False))
    print(json.dumps(call("GET", "/api/tickets/summary"), indent=2))
    print("DEMO OK: fluxo, conflitos e auditoria conferidos.")

if __name__ == "__main__":
    try:
        main()
    except (urllib.error.URLError, RuntimeError, ValueError, KeyError, StopIteration) as error:
        print(f"Falha na demonstracao: {error}. Confira se o JAR esta ativo e se a URL esta correta.", file=sys.stderr)
        sys.exit(1)
