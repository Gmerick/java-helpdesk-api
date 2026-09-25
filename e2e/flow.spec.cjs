const {test,expect}=require('@playwright/test');
test('criar chamado, acompanhar ciclo e conferir histórico',async({page})=>{
 await page.goto('/');await expect(page.locator('#workspace')).toHaveAttribute('aria-busy','false');
 await page.getByRole('button',{name:'Novo chamado',exact:true}).click();
 await page.getByLabel('Título da solicitação').fill('VPN indisponível — teste de interface');
 await page.getByLabel('O que aconteceu?').fill('Sem acesso ao ambiente de laboratório.');
 await page.getByLabel('Prioridade',{exact:true}).selectOption('HIGH');
 await page.getByRole('button',{name:'Criar chamado',exact:true}).click();
 const row=page.getByRole('row').filter({hasText:'VPN indisponível — teste de interface'});
 await expect(row).toContainText('Aberto');await row.getByRole('button').click();
 for(const action of ['Iniciar atendimento','Registrar solução','Encerrar chamado']){
  await page.getByRole('button',{name:action,exact:true}).click();
  await page.getByLabel('Justificativa').fill(action+' — evidência de teste');
  await page.getByRole('button',{name:'Confirmar alteração'}).click();
  await expect(page.getByRole('dialog')).not.toBeVisible();await row.getByRole('button').click();
 }
 await expect(page.locator('.history li')).toHaveCount(4);
 await expect(page.getByRole('button',{name:'Reabrir atendimento'})).toHaveCount(0);
 await page.getByRole('button',{name:'Fechar',exact:true}).click();
 await expect(row).toContainText('Encerrado');
 await page.getByLabel('Filtrar por status').selectOption('OPEN');await expect(row).toHaveCount(0);
});
test('texto do usuário é exibido sem executar HTML',async({page,request})=>{
 const title='<img src=x onerror=alert(1)>';
 await request.post('/api/tickets',{data:{title,description:'Teste de conteúdo literal',priority:'LOW'}});
 await page.goto('/');await expect(page.getByRole('row').filter({hasText:title})).toBeVisible();await expect(page.locator('tbody img')).toHaveCount(0);
});
