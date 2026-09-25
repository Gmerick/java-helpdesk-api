'use strict';
const $ = (selector) => document.querySelector(selector);
const esc = (value) => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money = value => value == null ? '—' : Number(value).toLocaleString('pt-BR',{style:'currency',currency:'BRL'});
const num = value => Number(value).toLocaleString('pt-BR');
const date = value => value ? new Date(String(value).replace(' ','T')).toLocaleString('pt-BR',{dateStyle:'short',timeStyle:'short'}) : '—';
const icons = {box:'<path d="m3 7 9-4 9 4-9 4-9-4Z M3 7v10l9 4 9-4V7 M12 11v10 M7 5l10 5"/>',grid:'<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',chart:'<path d="M4 3v17h17 M8 15v-4 M13 15V7 M18 15V4"/>',book:'<path d="M12 5v15 M3 4h5l4 2 4-2h5v15h-5l-4 2-4-2H3Z"/>',plus:'<path d="M12 5v14 M5 12h14"/>',check:'<path d="m5 12 4 4L19 6"/>',clock:'<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',users:'<circle cx="9" cy="8" r="3"/><path d="M3 21v-3a6 6 0 0 1 12 0v3 M16 5a3 3 0 0 1 0 6 M18 15a5 5 0 0 1 3 5"/>',ticket:'<path d="M4 4h16v5a3 3 0 0 0 0 6v5H4v-5a3 3 0 0 0 0-6V4Z M13 6v3 M13 11v2 M13 15v3"/>',info:'<circle cx="12" cy="12" r="9"/><path d="M12 11v6 M12 7v1"/>'};
const icon = name => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${icons[name]||icons.grid}</svg>`;
const statusNames={OPEN:'Aberto',IN_PROGRESS:'Em atendimento',RESOLVED:'Resolvido',CLOSED:'Encerrado',NEW:'Novo',CONVERTED:'Convertido'};
const badge=(status)=>`<span class="tag ${({IN_PROGRESS:'amber',RESOLVED:'green',CLOSED:'gray',CONVERTED:'green'})[status]||''}">${esc(statusNames[status]||status)}</span>`;
let notifyTimer;
function notify(message){$('#toast').textContent=message;$('#toast').hidden=false;clearTimeout(notifyTimer);notifyTimer=setTimeout(()=>$('#toast').hidden=true,5500);}
async function api(path,method='GET',body){
 let response;
 try{response=await fetch(path,{method,headers:body?{'Content-Type':'application/json'}:{},body:body?JSON.stringify(body):undefined,signal:AbortSignal.timeout(15000)});}
 catch(e){throw new Error(method==='GET'?'Não foi possível conectar. Confira se o programa está aberto e tente novamente.':'Não foi possível confirmar a operação. Atualize a lista antes de reenviar para evitar duplicidade.');}
 const data=await response.json().catch(()=>null);
 if(!response.ok) throw new Error(data?.detail||'Não foi possível concluir a operação. Tente novamente.');
 return data;
}
function shell(config){
 document.title=`${config.brand} · ${config.title}`;
 document.body.innerHTML=`<a class="skip" href="#main">Pular para o conteúdo</a><aside class="sidebar"><div class="brand"><span class="brand-icon">${icon(config.icon)}</span>${config.brand}</div><div class="workspace-label">ÁREA DE TRABALHO</div><nav aria-label="Navegação principal"><button class="nav-button active" id="nav-home" aria-current="page">${icon('grid')}Visão geral</button><button class="nav-button" id="nav-guide">${icon('book')}Como usar</button></nav><div class="side-bottom"><span class="avatar">EG</span><strong>Erick Gomes</strong>Projetos Java · Portfólio<br><br>Organização que simplifica<br>o seu dia a dia.</div></aside><div class="app"><header class="topbar"><div class="crumb">Workspace <span>/</span> ${config.title}</div><div class="local-badge"><i></i>Ambiente local</div></header><main id="main"><div class="heading"><div><p class="eyebrow">${config.eyebrow}</p><h1>${config.title}</h1><p class="sub">${config.subtitle}</p></div><button class="primary" id="create">${icon('plus')}${config.create}</button></div><div id="error" class="error-banner" role="alert" hidden><span></span><button class="secondary" id="retry">Tentar novamente</button></div><div class="cards" id="stats" aria-label="Indicadores"></div><div id="workspace" aria-busy="true"></div><div class="tip">${icon('info')}<div><strong>${config.tipTitle}</strong><p>${config.tip}</p></div></div><footer><span>${config.brand} · Feito para simplificar sua rotina</span><span>Portfólio demonstrativo · Use dados fictícios</span></footer></main></div><dialog id="dialog" aria-labelledby="dialog-title"><div class="dialog-head"><h2 id="dialog-title"></h2><button class="close" aria-label="Fechar">×</button></div><div class="dialog-content" id="dialog-content"></div></dialog><div id="toast" class="toast" role="status" hidden></div>`;
 $('#dialog .close').onclick=()=>$('#dialog').close();
 $('#nav-home').onclick=()=>{$('#main').scrollIntoView({behavior:'smooth'});};
 $('#nav-guide').onclick=()=>openDialog('Comece por aqui',`<div class="guide"><p>${config.subtitle}</p><ol>${config.guide.map(x=>`<li>${x}</li>`).join('')}</ol><p>Os dados ficam salvos neste computador. Para sair, feche a janela do programa. Cadastros e alterações são feitos apenas quando você confirma um formulário.</p></div>`);
}
function stats(items){$('#stats').innerHTML=items.map(x=>`<div class="stat"><div class="stat-icon">${icon(x[3]||'chart')}</div><span class="stat-label">${esc(x[0])}</span><div class="stat-number">${esc(x[1])}</div><span class="stat-note">${esc(x[2])}</span></div>`).join('');}
function openDialog(title,content){$('#dialog-title').textContent=title;$('#dialog-content').innerHTML=content;if(!$('#dialog').open)$('#dialog').showModal();}
function field(name,label,type='text',options={}){
 const attrs=`id="f-${name}" name="${name}" ${options.optional?'':'required'} ${options.maxLength?`maxlength="${options.maxLength}"`:''} ${options.min!=null?`min="${options.min}"`:''} ${options.max!=null?`max="${options.max}"`:''} ${options.step?`step="${options.step}"`:''} ${options.pattern?`pattern="${options.pattern}"`:''} ${options.hint?`aria-describedby="hint-${name}"`:''}`;
 let control=type==='textarea'?`<textarea ${attrs}>${esc(options.value||'')}</textarea>`:type==='select'?`<select ${attrs}>${options.choices.map(([v,l])=>`<option value="${esc(v)}">${esc(l)}</option>`).join('')}</select>`:`<input type="${type}" ${attrs} value="${esc(options.value??'')}" ${options.placeholder?`placeholder="${esc(options.placeholder)}"`:''}>`;
 return `<div><label for="f-${name}">${label}</label>${control}${options.hint?`<small class="hint" id="hint-${name}">${options.hint}</small>`:''}</div>`;
}
function form(title,fields,submit,save='Salvar',intro=''){
 openDialog(title,`${intro}<form id="edit-form"><div class="form-grid">${fields}</div><div class="form-error" id="form-error" role="alert" hidden></div><div class="form-actions"><button type="button" class="secondary" id="cancel">Cancelar</button><button type="submit" class="primary">${save}</button></div></form>`);
 $('#cancel').onclick=()=>$('#dialog').close();
 $('#edit-form').onsubmit=async event=>{
  event.preventDefault();const f=event.currentTarget;if(f.dataset.saving)return;f.dataset.saving='1';const button=f.querySelector('[type=submit]');button.disabled=true;button.textContent='Salvando…';$('#form-error').hidden=true;
  const close=$('#dialog .close'),cancel=$('#cancel'),dialog=$('#dialog');close.disabled=true;cancel.disabled=true;const preventClose=e=>e.preventDefault();dialog.addEventListener('cancel',preventClose);
  try{const values=Object.fromEntries(new FormData(f));f.querySelectorAll('[type=number]').forEach(e=>values[e.name]=Number(e.value));await submit(values);dialog.close();notify('Salvo com sucesso.');await refresh();}
  catch(error){$('#form-error').textContent=error.message;$('#form-error').hidden=false;}
  finally{f.dataset.saving='';button.disabled=false;button.textContent=save;close.disabled=false;cancel.disabled=false;dialog.removeEventListener('cancel',preventClose);}
 };
}
function empty(message='Nenhum registro por aqui',hint='Comece pelo botão acima para adicionar o primeiro registro.'){return `<div class="empty"><strong>${message}</strong><p>${hint}</p></div>`;}
function table(headers,rows){return `<div class="table-scroll"><table><thead><tr>${headers.map(x=>`<th scope="col">${x}</th>`).join('')}</tr></thead><tbody>${rows.join('')}</tbody></table></div>`;}
function pageControls(offset,hasNext){return `<div class="pagination"><span>Página ${Math.floor(offset/20)+1} · até 20 registros</span><div><button class="secondary" id="prev" ${offset?'':'disabled'}>Anterior</button><button class="secondary" id="next" ${hasNext?'':'disabled'}>Próxima</button></div></div>`;}
function showError(error){$('#error span').textContent=error.message;$('#error').hidden=false;}
