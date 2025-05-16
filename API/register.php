<?php
require 'config.php';
$in = json_decode(file_get_contents('php://input'), true);

// Validaciones
if (empty($in['email']) || empty($in['password']) || empty($in['confirm_password'])) {
    http_response_code(400);
    echo json_encode(['error'=>'Email y ambas contraseñas obligatorios']);
    exit;
}
if (!filter_var($in['email'],FILTER_VALIDATE_EMAIL)) {
    http_response_code(400);
    echo json_encode(['error'=>'Email inválido']);
    exit;
}
if ($in['password'] !== $in['confirm_password']) {
    http_response_code(400);
    echo json_encode(['error'=>'Las contraseñas no coinciden']);
    exit;
}
// Complejidad básica
$pw = $in['password'];
$errs = [];
if (strlen($pw)<8)           $errs[]='>=8 caracteres';
if (!preg_match('/[a-z]/',$pw)) $errs[]='minuscula';
if (!preg_match('/[A-Z]/',$pw)) $errs[]='mayuscula';
if (!preg_match('/\d/',$pw))     $errs[]='numero';
if (!preg_match('/\W/',$pw))     $errs[]='simbolo';
if ($errs) {
    http_response_code(400);
    echo json_encode(['error'=>'Faltan: '.implode(',', $errs)]);
    exit;
}

$hashCurr   = password_hash($pw, PASSWORD_BCRYPT);
$hashMaster = $hashCurr;

// Inserción
$stmt = $pdo->prepare("
  INSERT INTO users (email,current_password_hash,master_password_hash)
  VALUES (?,?,?)
");
try {
  $stmt->execute([$in['email'],$hashCurr,$hashMaster]);
  http_response_code(201);
  echo json_encode(['message'=>'Usuario creado']);
} catch (PDOException $e) {
  http_response_code(409);
  echo json_encode(['error'=>'Email ya existe']);
}
