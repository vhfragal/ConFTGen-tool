(define-sort Feature () Bool)
(declare-const R Feature)
(declare-const A Feature)
(declare-const B Feature)
(declare-const C Feature)

(assert R)
(assert (and
   (=> A R)
   (=> B R)
   (=> C R)
))

(push)
(assert (and (and (and A B (and A B C)) C (and A B C)) (and B C (and (not A) B C))))
(check-sat)
(pop)
