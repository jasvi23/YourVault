<?php
require 'config.php';
$in = json_decode(file_get_contents('php://input'), true);

if (empty($in['email'])||empty($in['password'])) {
    http_response_code(400);
    echo json_encode(['error'=>'Email y contraseña obligatorios']);
    exit;
}

$stmt = $pdo->prepare("
  SELECT id,current_password_hash 
    FROM users WHERE email=?
");
$stmt->execute([$in['email']]);
$user = $stmt->fetch();

if (!$user || !password_verify($in['password'],$user['current_password_hash'])) {
    http_response_code(401);
    echo json_encode(['error'=>'Credenciales inválidas']);
    exit;
}

// Devolvemos sólo el ID; el masterPassword lo tendrá el usuario al loguearse
echo json_encode(['userId'=>$user['id']]);

