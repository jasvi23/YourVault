<?php
require 'config.php';
$in = json_decode(file_get_contents('php://input'), true);
if (empty($in['email'])||empty($in['new_password'])) {
  http_response_code(400);
  echo json_encode(['error'=>'Email y contraseña obligatorios']);
  exit;
}
$stmt = $pdo->prepare("SELECT id FROM users WHERE email=?");
$stmt->execute([$in['email']]);
$user = $stmt->fetch();
if (!$user) {
  http_response_code(404);
  echo json_encode(['error'=>'Usuario no encontrado']);
  exit;
}
$hash = password_hash($in['new_password'],PASSWORD_BCRYPT);
$upd = $pdo->prepare("
  UPDATE users 
    SET current_password_hash = ? 
    WHERE id = ?
");
$upd->execute([$hash, $user['id']]);
echo json_encode(['message'=>'Contraseña actualizada']);
