from pathlib import Path
import hashlib, zipfile
root = Path(__file__).resolve().parents[1]
name = root.name
out = root / 'dist'
out.mkdir(exist_ok=True)
archive = out / (name + '-windows.zip')
with zipfile.ZipFile(archive, 'w', zipfile.ZIP_DEFLATED) as z:
    z.write(root / 'target/app.jar', name + '/app.jar')
    z.write(root / 'scripts/INICIAR.cmd', name + '/INICIAR.cmd')
    z.write(root / 'docs/INTERFACE.md', name + '/LEIA-ME.md')
(out / (archive.name + '.sha256')).write_text(hashlib.sha256(archive.read_bytes()).hexdigest() + '  ' + archive.name + '\n')
print(archive)
