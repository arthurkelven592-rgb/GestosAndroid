# Gestos Android — estrutura inicial

Este projeto é um ponto de partida nativo para Android, pensado para edição pelo celular e compilação remota.

## O que já está incluído
- Aplicativo Android com botões grandes.
- Serviço de acessibilidade declarado com permissão para executar gestos.
- Ações de sistema: Voltar, Início e Notificações.
- Reconhecimento de voz sob demanda, com os comandos “voltar”, “início” e “notificações”.

## O que ainda não está integrado
O detector de mãos do HTML/MediaPipe, o cursor flutuante global e a pinça que toca em qualquer aplicativo ainda precisam ser ligados ao serviço nativo. Esta estrutura NÃO é ainda a versão final com controle pela câmera.

## Como testar a estrutura
1. Abra o projeto no GitHub.
2. Use GitHub Actions com um workflow de Android/Gradle para compilar `assembleDebug`.
3. Baixe o APK gerado como artefato do workflow e instale no Android.
4. Abra Gestos > “Ativar acessibilidade” e ative “Gestos - Acessibilidade”.
5. Teste os botões Voltar e Início.
6. Toque em “Ativar comandos de voz” e diga “voltar”, “início” ou “notificações”.

## Integração com seu HTML
Copie seu `index.html` para `app/src/main/assets/index.html` e configure uma WebView para carregá-lo. Para comunicação segura entre JavaScript e Kotlin, exponha uma interface limitada, com métodos explícitos como `onPinch(x, y)` e `onCursor(x, y)`. O método de pinça deve chamar `GestosAccessibilityService.instance?.tocar(x, y)` somente após validar coordenadas e quando o serviço estiver ativado.

Não exponha métodos genéricos que permitam executar código arbitrário. O reconhecimento contínuo de câmera em segundo plano exige desenho adicional, permissões e uma notificação visível; não é fornecido por este esqueleto.

## Permissões e privacidade
- A pessoa deve ativar o serviço manualmente nas configurações.
- O microfone só é usado após tocar no botão de voz.
- O reconhecimento de voz pode usar serviços do dispositivo ou da rede, dependendo da configuração Android.
- Não mantenha câmera ou microfone ativos sem indicação clara.
