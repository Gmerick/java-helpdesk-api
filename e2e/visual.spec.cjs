const {test,expect}=require('@playwright/test');
test('revisão visual desktop e celular',async({page,request})=>{
 for(const [title,priority,state] of [['Configuração de novo notebook','MEDIUM','IN_PROGRESS'],['Instalação de impressora','LOW','OPEN'],['Acesso ao sistema de gestão','HIGH','OPEN'],['Atualização de software','MEDIUM','RESOLVED']]){
 const t=await(await request.post('/api/tickets',{data:{title,priority,description:'Solicitação fictícia para demonstração do atendimento.'}})).json();
 if(state!=='OPEN')await request.patch(`/api/tickets/${t.id}/status`,{data:{status:'IN_PROGRESS',note:'Atendimento iniciado'}});
 if(state==='RESOLVED')await request.patch(`/api/tickets/${t.id}/status`,{data:{status:'RESOLVED',note:'Configuração concluída'}});
 }
 await page.goto('/');await expect(page.locator('#workspace')).toHaveAttribute('aria-busy','false');await expect(page.locator('#error')).toBeHidden();
 await page.screenshot({path:'test-results/overview-desktop.png',fullPage:true});
 await page.setViewportSize({width:390,height:844});
 expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBeTruthy();
 await page.screenshot({path:'test-results/overview-mobile.png',fullPage:true});
});
